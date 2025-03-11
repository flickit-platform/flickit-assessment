package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.assessment.GetAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.GetAttributeInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.GetSubjectInsightHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightsIssuesService implements GetAssessmentInsightsIssuesUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final GetAssessmentInsightHelper getAssessmentInsightHelper;
    private final GetSubjectInsightHelper getSubjectInsightHelper;
    private final GetAttributeInsightHelper getAttributeInsightHelper;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final LoadAttributeValuePort loadAttributeValuePort;

    @Override
    public Result getInsightsIssues(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentInsight = getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId());
        var subjectsInsightMap = getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId());
        var attributesInsightMap = getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId());

        var subjects = buildSubject(param, assessmentResult, subjectsInsightMap, attributesInsightMap);

        var subjectsCount = subjects.size();
        var attributesCount = subjects.stream()
            .flatMap(s -> s.attributes().stream())
            .toList().size();

        var issues = buildIssues(assessmentInsight,
            subjectsInsightMap,
            attributesInsightMap,
            assessmentResult.getLastCalculationTime(),
            subjectsCount,
            attributesCount);
        return new Result(issues.notGenerated(), issues.unapproved(), issues.expired());
    }

    private List<SubjectModel> buildSubject(Param param,
                                            AssessmentResult assessmentResult,
                                            Map<Long, Insight> subjectsInsightMap,
                                            Map<Long, Insight> attributesInsightMap) {
        var subjectValues = loadSubjectValuePort.loadAll(assessmentResult.getId());
        var attributeIdToValueMap = loadAttributeValuePort.loadAll(assessmentResult.getId()).stream()
            .collect(toMap(attributeValue -> attributeValue.getAttribute().getId(), Function.identity()));
        var subjectInsightEditable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            CREATE_SUBJECT_INSIGHT);
        var attributeInsightEditable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            CREATE_ATTRIBUTE_INSIGHT);

        return subjectValues.stream()
            .map(subjectValue -> toSubject(subjectValue,
                subjectsInsightMap.get(subjectValue.getSubject().getId()),
                subjectInsightEditable,
                attributeIdToValueMap,
                attributesInsightMap,
                attributeInsightEditable)
            ).toList();
    }

    private SubjectModel toSubject(SubjectValue subjectValue,
                                   Insight subjectInsight,
                                   boolean subjectInsightEditable,
                                   Map<Long, AttributeValue> attributeIdToValueMap,
                                   Map<Long, Insight> attributesInsightMap,
                                   boolean attributeInsightEditable) {
        var subject = subjectValue.getSubject();
        return new SubjectModel(
            subject.getId(),
            subject.getTitle(),
            subject.getDescription(),
            subject.getIndex(),
            subject.getWeight(),
            MaturityLevelModel.of(subjectValue.getMaturityLevel()),
            subjectValue.getConfidenceValue(),
            InsightModel.of(subjectInsight, subjectInsightEditable),
            buildAttributes(subject.getAttributes(), attributeIdToValueMap, attributesInsightMap, attributeInsightEditable)
        );
    }

    private List<AttributeModel> buildAttributes(List<Attribute> attributes,
                                                 Map<Long, AttributeValue> attributeIdToValueMap,
                                                 Map<Long, Insight> attributesInsightMap,
                                                 boolean editable) {
        return attributes.stream()
            .map(attribute -> AttributeModel.of(attribute,
                attributeIdToValueMap.get(attribute.getId()),
                attributesInsightMap.get(attribute.getId()),
                editable))
            .toList();
    }

    private Issues buildIssues(Insight assessmentInsight,
                               Map<Long, Insight> subjectsInsightMap,
                               Map<Long, Insight> attributesInsightMap,
                               LocalDateTime lastCalculationTime,
                               int subjectsCount,
                               int attributesCount) {
        var subjectsInsights = subjectsInsightMap.values().stream().toList();
        var attributesInsights = attributesInsightMap.values().stream().toList();

        var expectedInsightsCount = attributesCount + subjectsCount + 1;
        var totalGeneratedInsights = attributesInsights.size() +
            subjectsInsights.size() +
            (Insight.isNotGenerated().test(assessmentInsight) ? 0 : 1);

        int notGeneratedInsights = Math.max(expectedInsightsCount - totalGeneratedInsights, 0);
        int unapprovedInsights = countUnapprovedInsights(assessmentInsight, subjectsInsights, attributesInsights);
        int expiredInsights = countExpiredInsights(assessmentInsight, subjectsInsightMap, attributesInsightMap, lastCalculationTime);

        return new Issues(notGeneratedInsights, unapprovedInsights, expiredInsights);
    }

    private int countUnapprovedInsights(Insight assessmentInsight,
                                        List<Insight> subjectsInsights,
                                        List<Insight> attributesInsights) {
        var assessmentInsightUnapproved = Insight.isUnapproved().test(assessmentInsight) ? 1 : 0;

        var unapprovedSubjectsInsightsCount = subjectsInsights.stream()
            .filter(Insight.isUnapproved())
            .toList().size();
        var unapprovedAttributesInsightsCount = attributesInsights.stream()
            .filter(Insight.isUnapproved())
            .toList().size();

        return assessmentInsightUnapproved + unapprovedSubjectsInsightsCount + unapprovedAttributesInsightsCount;
    }

    private int countExpiredInsights(Insight assessmentInsight,
                                     Map<Long, Insight> subjectsInsightMap,
                                     Map<Long, Insight> attributesInsightMap,
                                     LocalDateTime lastCalculationTime) {
        var assessmentInsightExpired = Insight.isExpired(lastCalculationTime).test(assessmentInsight) ? 1 : 0;

        var expiredSubjectsInsightsCount = subjectsInsightMap.values().stream()
            .filter(Insight.isExpired(lastCalculationTime))
            .toList().size();
        var expiredAttributeInsightsCount = attributesInsightMap.values().stream()
            .filter(Insight.isExpired(lastCalculationTime))
            .toList().size();

        return expiredAttributeInsightsCount + expiredSubjectsInsightsCount + assessmentInsightExpired;
    }
}
