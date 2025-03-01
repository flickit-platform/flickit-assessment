package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.in.insight.ApproveExpiredInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.ApproveAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.ApproveAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.ApproveSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveExpiredInsightsService implements ApproveExpiredInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final ApproveAttributeInsightPort approveAttributeInsightPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final ApproveSubjectInsightPort approveSubjectInsightPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final ApproveAssessmentInsightPort approveAssessmentInsightPort;

    @Override
    public void approveExpiredInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        approveExpiredAttributesInsight(assessmentResult);
        approveExpiredSubjectsInsight(assessmentResult);
        approveExpiredAssessmentInsight(assessmentResult);
    }

    private void approveExpiredAttributesInsight(AssessmentResult assessmentResult) {
        var expiredInsightIds = loadAttributeInsightsPort.loadInsights(assessmentResult.getId()).stream()
            .filter(e -> e.getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime()))
            .map(AttributeInsight::getAttributeId)
            .toList();
        if (!expiredInsightIds.isEmpty()) {
            approveAttributeInsightPort.approveAll(assessmentResult.getAssessment().getId(),
                expiredInsightIds,
                LocalDateTime.now());
        }
    }

    private void approveExpiredSubjectsInsight(AssessmentResult assessmentResult) {
        var expiredInsightIds = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()).stream()
            .filter(e -> e.getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime()))
            .map(SubjectInsight::getSubjectId)
            .toList();
        if (!expiredInsightIds.isEmpty()) {
            approveSubjectInsightPort.approveAll(assessmentResult.getAssessment().getId(),
                expiredInsightIds,
                LocalDateTime.now());
        }
    }

    private void approveExpiredAssessmentInsight(AssessmentResult assessmentResult) {
        var loadedInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (loadedInsight.isPresent() &&
            loadedInsight.get().getLastModificationTime().isBefore(assessmentResult.getLastCalculationTime())) {
            approveAssessmentInsightPort.approve(assessmentResult.getAssessment().getId(), LocalDateTime.now());
        }
    }
}
