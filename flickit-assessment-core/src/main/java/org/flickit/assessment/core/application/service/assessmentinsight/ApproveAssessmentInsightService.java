package org.flickit.assessment.core.application.service.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentinsight.ApproveAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinsight.ApproveAssessmentInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ASSIGNMENT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAssessmentInsightService implements ApproveAssessmentInsightUseCase {

    private final ApproveAssessmentInsightPort approveAssessmentInsightPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void approveAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ASSIGNMENT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveAssessmentInsightPort.approve(param.getAssessmentId(), LocalDateTime.now());
    }
}
