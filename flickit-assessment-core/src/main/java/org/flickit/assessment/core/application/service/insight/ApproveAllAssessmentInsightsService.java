package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.insight.ApproveAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.ApproveAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.ApproveAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.ApproveSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAllAssessmentInsightsService implements ApproveAllAssessmentInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final ApproveAssessmentInsightPort approveAssessmentInsightPort;
    private final ApproveSubjectInsightPort approveSubjectInsightPort;
    private final ApproveAttributeInsightPort approveAttributeInsightPort;

    @Override
    public void approveAllAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveAssessmentInsight(param.getAssessmentId());
        approveSubjectInsightPort.approveAll(param.getAssessmentId(), LocalDateTime.now());
        approveAttributeInsightPort.approveAll(param.getAssessmentId(), LocalDateTime.now());
    }

    private void approveAssessmentInsight(UUID assessmentId) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (assessmentInsight.isPresent() && !Boolean.TRUE.equals(assessmentInsight.get().isApproved()))
            approveAssessmentInsightPort.approve(assessmentId, LocalDateTime.now());
    }
}
