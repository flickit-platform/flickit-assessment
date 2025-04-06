package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.in.insight.RegenerateExpiredInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightParam;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightsParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class RegenerateExpiredInsightsService implements RegenerateExpiredInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final CreateAttributeAiInsightHelper createAttributeAiInsightHelper;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final CreateSubjectInsightsHelper createSubjectInsightsHelper;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final CreateAssessmentInsightHelper createAssessmentInsightHelper;
    private final UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Override
    public void regenerateExpiredInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());
        regenerateExpiredAttributesInsight(assessmentResult, locale);
        regenerateExpiredSubjectsInsight(assessmentResult, locale);
        regenerateExpiredAssessmentInsight(assessmentResult, locale);
    }

    private void regenerateExpiredAttributesInsight(AssessmentResult assessmentResult, Locale locale) {
        var expiredInsightIds = loadAttributeInsightsPort.loadInsights(assessmentResult.getId()).stream()
            .filter(e -> e.getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime()))
            .map(AttributeInsight::getAttributeId)
            .toList();
        if (!expiredInsightIds.isEmpty())
            updateAttributesInsights(assessmentResult, expiredInsightIds, locale);
    }

    private void updateAttributesInsights(AssessmentResult assessmentResult, List<Long> attributeIds, Locale locale) {
        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        var insightUpdateParams = attributeIds.stream()
            .map(id -> {
                var createAiInsightParam = new AttributeInsightParam(assessmentResult,
                    id,
                    maturityLevels,
                    progress,
                    locale);
                var attributeAiInsight = createAttributeAiInsightHelper.createAttributeAiInsight(createAiInsightParam);
                return toUpdateParam(attributeAiInsight);
            })
            .toList();
        updateAttributeInsightPort.updateAiInsights(insightUpdateParams);
    }

    private UpdateAttributeInsightPort.AiParam toUpdateParam(AttributeInsight attributeAiInsight) {
        return new UpdateAttributeInsightPort.AiParam(attributeAiInsight.getAssessmentResultId(),
            attributeAiInsight.getAttributeId(),
            attributeAiInsight.getAiInsight(),
            attributeAiInsight.getAiInsightTime(),
            attributeAiInsight.getAiInputPath(),
            attributeAiInsight.isApproved(),
            attributeAiInsight.getLastModificationTime());
    }

    private void regenerateExpiredSubjectsInsight(AssessmentResult assessmentResult, Locale locale) {
        var expiredInsightIds = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()).stream()
            .filter(e -> e.getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime()))
            .map(SubjectInsight::getSubjectId)
            .toList();
        if (!expiredInsightIds.isEmpty()) {
            var insights = createSubjectInsightsHelper
                .createSubjectInsights(new SubjectInsightsParam(assessmentResult, expiredInsightIds, locale));
            updateSubjectInsightPort.updateAll(insights);
        }
    }

    private void regenerateExpiredAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var loadedInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (loadedInsight.isPresent() &&
            loadedInsight.get().getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime())) {
            var insight = createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale);
            updateAssessmentInsightPort.updateInsight(assignIdToAssessmentInsight(loadedInsight.get().getId(), insight));
        }
    }

    private AssessmentInsight assignIdToAssessmentInsight(UUID id, AssessmentInsight assessmentInsight) {
        return new AssessmentInsight(id,
            assessmentInsight.getAssessmentResultId(),
            assessmentInsight.getInsight(),
            assessmentInsight.getInsightTime(),
            assessmentInsight.getLastModificationTime(),
            assessmentInsight.getInsightBy(),
            assessmentInsight.isApproved());
    }
}
