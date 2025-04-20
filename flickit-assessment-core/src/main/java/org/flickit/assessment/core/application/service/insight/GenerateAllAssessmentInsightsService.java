package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.in.insight.GenerateAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightsParam;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightsParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerateAllAssessmentInsightsService implements GenerateAllAssessmentInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CreateAttributeAiInsightHelper createAttributeAiInsightHelper;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final CreateSubjectInsightsHelper createSubjectInsightsHelper;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final CreateAssessmentInsightHelper createAssessmentInsightHelper;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;

    @Override
    public void generateAllAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        initAttributesInsight(param.getAssessmentId(), assessmentResult, locale);
        initSubjectsInsight(assessmentResult, locale);
        initAssessmentInsight(assessmentResult, locale);
    }

    private void initAttributesInsight(UUID assessmentId, AssessmentResult assessmentResult, Locale locale) {
        var attributeIds = loadAttributesPort.loadAll(assessmentId).stream()
            .map(LoadAttributesPort.Result::id)
            .collect(toList());
        var attributeInsightIds = loadAttributeInsightsPort.loadInsights(assessmentResult.getId()).stream()
            .map(AttributeInsight::getAttributeId)
            .toList();
        attributeIds.removeAll(attributeInsightIds);
        if (!attributeIds.isEmpty()) {
            var attributeAiInsights = createAttributeAiInsightHelper
                .createAttributeAiInsights(new AttributeInsightsParam(assessmentResult, attributeIds, locale));
            createAttributeInsightPort.persistAll(attributeAiInsights);
        }
    }

    private void initSubjectsInsight(AssessmentResult assessmentResult, Locale locale) {
        var subjectIds = loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()).stream()
            .map(Subject::getId)
            .collect(toList());
        var subjectInsightIds = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()).stream()
            .map(SubjectInsight::getSubjectId)
            .toList();
        subjectIds.removeAll(subjectInsightIds);
        if (!subjectIds.isEmpty()) {
            var subjectInsights = createSubjectInsightsHelper
                .createSubjectInsights(new SubjectInsightsParam(assessmentResult, subjectIds, locale));
            createSubjectInsightPort.persistAll(subjectInsights);
        }
    }

    private void initAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var loadedAssessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (loadedAssessmentInsight.isEmpty()) {
            var assessmentInsight = createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale);
            createAssessmentInsightPort.persist(assessmentInsight);
        }
    }
}
