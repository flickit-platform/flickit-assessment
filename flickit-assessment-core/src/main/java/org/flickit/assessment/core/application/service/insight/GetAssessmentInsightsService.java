package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.GetAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.CountMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.assessment.GetAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.GetAttributeInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.GetSubjectInsightHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightsService implements GetAssessmentInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final GetAssessmentInsightHelper getAssessmentInsightHelper;
    private final GetSubjectInsightHelper getSubjectInsightHelper;
    private final GetAttributeInsightHelper getAttributeInsightHelper;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final CountMaturityLevelsPort countMaturityLevelsPort;

    @Override
    public Result getAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentInsight = getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId());
        var subjectsInsightMap = getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId());
        var attributesInsightMap = getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId());

        var assessment = buildAssessment(assessmentResult, assessmentInsight);
        var subjects = buildSubject(param, assessmentResult, subjectsInsightMap, attributesInsightMap);
        var issues = buildIssues(assessment,
            subjects,
            assessmentInsight,
            subjectsInsightMap,
            attributesInsightMap,
            assessmentResult.getLastCalculationTime());
        return new Result(assessment, subjects, issues);
    }

    private Assessment buildAssessment(AssessmentResult assessmentResult, Insight assessmentInsight) {
        return new Assessment(assessmentResult.getAssessment().getId(),
            assessmentResult.getAssessment().getTitle(),
            toMaturityLevel(assessmentResult.getMaturityLevel()),
            assessmentResult.getConfidenceValue(),
            assessmentResult.getIsCalculateValid(),
            assessmentResult.getIsConfidenceValid(),
            toInsight(assessmentInsight, assessmentInsight.editable()),
            new Kit(countMaturityLevelsPort.count(assessmentResult.getKitVersionId())));
    }

    private MaturityLevelModel toMaturityLevel(MaturityLevel maturityLevel) {
        return new MaturityLevelModel(maturityLevel.getId(),
            maturityLevel.getTitle(),
            maturityLevel.getValue(),
            maturityLevel.getIndex());
    }

    private List<SubjectModel> buildSubject(Param param,
                                            AssessmentResult assessmentResult,
                                            Map<Long, Insight> subjectsInsightMap,
                                            Map<Long, Insight> attributesInsightMap) {
        var subjectValues = loadSubjectValuePort.loadAll(assessmentResult.getId());
        var attributeValuesMap = loadAttributeValuePort.loadAll(assessmentResult.getId()).stream()
            .collect(toMap(attributeValue -> attributeValue.getAttribute().getId(), Function.identity()));
        var subjectInsightEditable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            CREATE_SUBJECT_INSIGHT);
        var attributeInsightEditable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            CREATE_ATTRIBUTE_INSIGHT);

        return subjectValues.stream()
            .map(subjectValue -> toSubject(subjectValue,
                subjectsInsightMap,
                subjectInsightEditable,
                attributeValuesMap,
                attributesInsightMap,
                attributeInsightEditable)
            ).toList();
    }

    private SubjectModel toSubject(SubjectValue subjectValue,
                                   Map<Long, Insight> subjectsInsightMap,
                                   boolean subjectInsightEditable,
                                   Map<Long, AttributeValue> attributeValuesMap,
                                   Map<Long, Insight> attributesInsightMap,
                                   boolean attributeInsightEditable) {
        var subject = subjectValue.getSubject();
        return new SubjectModel(
            subject.getId(),
            subject.getTitle(),
            subject.getDescription(),
            subject.getIndex(),
            subject.getWeight(),
            toMaturityLevel(subjectValue.getMaturityLevel()),
            subjectValue.getConfidenceValue(),
            toInsight(subjectsInsightMap.get(subject.getId()), subjectInsightEditable),
            buildAttributes(subject.getAttributes(), attributeValuesMap, attributesInsightMap, attributeInsightEditable)
        );
    }

    private InsightModel toInsight(Insight insight, boolean editable) {
        return insight != null
            ? new InsightModel(toInsightDetail(insight.defaultInsight()),
            toInsightDetail(insight.assessorInsight()),
            insight.editable(),
            insight.approved())
            : new InsightModel(null, null, editable, null);
    }

    private InsightModel.InsightDetail toInsightDetail(Insight.InsightDetail insightDetail) {
        return insightDetail != null
            ? new InsightModel.InsightDetail(insightDetail.insight(), insightDetail.creationTime(), insightDetail.isValid())
            : null;
    }

    private List<AttributeModel> buildAttributes(List<Attribute> attributes,
                                                 Map<Long, AttributeValue> attributeValuesMap,
                                                 Map<Long, Insight> attributesInsightMap,
                                                 boolean editable) {
        return attributes.stream()
            .map(attribute -> toAttribute(attribute, attributeValuesMap, attributesInsightMap, editable))
            .toList();
    }

    private AttributeModel toAttribute(Attribute attribute,
                                       Map<Long, AttributeValue> attributeValuesMap,
                                       Map<Long, Insight> attributesInsightMap,
                                       boolean editable) {
        return new AttributeModel(attribute.getId(),
            attribute.getTitle(),
            attribute.getDescription(),
            attribute.getIndex(),
            attribute.getWeight(),
            toMaturityLevel(attributeValuesMap.get(attribute.getId()).getMaturityLevel()),
            attributeValuesMap.get(attribute.getId()).getConfidenceValue(),
            toInsight(attributesInsightMap.get(attribute.getId()), editable));
    }

    private Issues buildIssues(Assessment assessment,
                               List<SubjectModel> subjects,
                               Insight assessmentInsight,
                               Map<Long, Insight> subjectsInsightMap,
                               Map<Long, Insight> attributesInsightMap,
                               LocalDateTime lastCalculationTime) {
        var subjectsInsights = subjects.stream()
            .map(SubjectModel::insight)
            .toList();
        var attributesInsights = subjects.stream()
            .flatMap(s -> s.attributes().stream())
            .map(AttributeModel::insight)
            .toList();

        int notGeneratedInsights = countNotGeneratedInsights(assessment.insight(), subjectsInsights, attributesInsights);
        int unapprovedInsights = countUnapprovedInsights(assessment.insight(), subjectsInsights, attributesInsights);
        int expiredInsights = countExpiredInsights(assessmentInsight, subjectsInsightMap, attributesInsightMap, lastCalculationTime);

        return new Issues(notGeneratedInsights, unapprovedInsights, expiredInsights);
    }

    private int countNotGeneratedInsights(InsightModel assessmentInsight,
                                          List<InsightModel> subjectsInsights,
                                          List<InsightModel> attributesInsights) {
        var assessmentInsightUnapproved = (int) Optional.of(assessmentInsight)
            .filter(isNotGenerated()).stream()
            .count();
        var notGeneratedSubjectsInsightsCount = (int) subjectsInsights.stream()
            .filter(isNotGenerated())
            .count();
        var notGeneratedAttributesInsightsCount = (int) attributesInsights.stream()
            .filter(isNotGenerated())
            .count();

        return assessmentInsightUnapproved + notGeneratedSubjectsInsightsCount + notGeneratedAttributesInsightsCount;
    }

    private Predicate<InsightModel> isNotGenerated() {
        return insight -> insight.defaultInsight() == null && insight.assessorInsight() == null;
    }

    private int countUnapprovedInsights(InsightModel assessmentInsight,
                                        List<InsightModel> subjectsInsights,
                                        List<InsightModel> attributesInsights) {
        var assessmentInsightUnapproved = (int) Optional.of(assessmentInsight)
            .filter(isUnapproved()).stream()
            .count();
        var unapprovedSubjectsInsightsCount = (int) subjectsInsights.stream()
            .filter(isUnapproved())
            .count();
        var unapprovedAttributesInsightsCount = (int) attributesInsights.stream()
            .filter(isUnapproved())
            .count();

        return assessmentInsightUnapproved + unapprovedSubjectsInsightsCount + unapprovedAttributesInsightsCount;
    }

    private Predicate<InsightModel> isUnapproved() {
        return insight -> {
            if (insight.approved() != null || insight.defaultInsight() != null)
                return !Boolean.TRUE.equals(insight.approved());
            return false;
        };
    }

    private int countExpiredInsights(Insight assessmentInsight,
                                     Map<Long, Insight> subjectsInsightMap,
                                     Map<Long, Insight> attributesInsightMap,
                                     LocalDateTime lastCalculationTime) {
        var assessmentInsightExpired = (int) Optional.of(assessmentInsight)
            .filter(isExpired(lastCalculationTime)).stream()
            .count();
        var expiredSubjectsInsightsCount = (int) subjectsInsightMap.values().stream()
            .filter(isExpired(lastCalculationTime))
            .count();
        var expiredAttributeInsightsCount = (int) attributesInsightMap.values().stream()
            .filter(isExpired(lastCalculationTime))
            .count();

        return expiredAttributeInsightsCount + expiredSubjectsInsightsCount + assessmentInsightExpired;
    }

    private Predicate<Insight> isExpired(LocalDateTime lastCalculationTime) {
        return insight -> {
            if (insight.assessorInsight() != null)
                return insight.assessorInsight().lastModificationTime().isBefore(lastCalculationTime);
            if (insight.defaultInsight() != null)
                return insight.defaultInsight().lastModificationTime().isBefore(lastCalculationTime);
            return false;
        };
    }
}
