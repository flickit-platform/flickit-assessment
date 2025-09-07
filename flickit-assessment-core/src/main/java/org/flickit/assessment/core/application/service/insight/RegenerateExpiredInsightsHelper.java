package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegenerateExpiredInsightsHelper {

    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final UpdateSubjectInsightPort updateSubjectInsightPort;
    private final UpdateAssessmentInsightPort updateAssessmentInsightPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CreateAttributeAiInsightHelper createAttributeAiInsightHelper;
    private final CreateSubjectInsightsHelper createSubjectInsightsHelper;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final CreateAssessmentInsightHelper createAssessmentInsightHelper;

    public void regenerateExpiredInsights(AssessmentResult assessmentResult, Locale locale) {
        regenerateExpiredAttributesInsight(assessmentResult, locale);
        regenerateExpiredSubjectsInsight(assessmentResult, locale);
        regenerateExpiredAssessmentInsight(assessmentResult, locale);
    }

    private void regenerateExpiredAttributesInsight(AssessmentResult assessmentResult, Locale locale) {
        var expiredInsightIds = loadAttributeInsightsPort.loadInsights(assessmentResult.getId()).stream()
            .filter(e -> e.getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime()))
            .map(AttributeInsight::getAttributeId)
            .toList();
        if (!expiredInsightIds.isEmpty()) {
            log.info("Regenerating expired attributes insights for attributeIds= [{}] of assessmentId=[{}]", expiredInsightIds, assessmentResult.getId());
            var insightUpdateParams = createAttributeAiInsightHelper
                .createAttributeAiInsights(new CreateAttributeAiInsightHelper.AttributeInsightsParam(assessmentResult, expiredInsightIds, locale)).stream()
                .map(this::toUpdateParam)
                .toList();
            updateAttributeInsightPort.updateAiInsights(insightUpdateParams);
        }
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
            log.info("Regenerating expired subjects insights for subjectIds= [{}] of assessmentId=[{}]", expiredInsightIds, assessmentResult.getId());
            var insights = createSubjectInsightsHelper
                .createSubjectInsights(new CreateSubjectInsightsHelper.SubjectInsightsParam(assessmentResult, expiredInsightIds, locale));
            updateSubjectInsightPort.updateAll(insights);
        }
    }

    private void regenerateExpiredAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var loadedInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (loadedInsight.isPresent() &&
            loadedInsight.get().getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime())) {
            log.info("Regenerating expired assessment insight for assessmentId=[{}]", assessmentResult.getAssessment().getId());
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
