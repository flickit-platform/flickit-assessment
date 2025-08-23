package org.flickit.assessment.core.application.service.insight.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.insight.subject.ApproveSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.insight.subject.ApproveSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveSubjectInsightService implements ApproveSubjectInsightUseCase {

    private final ApproveSubjectInsightPort approveSubjectInsightPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void approveSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_SUBJECT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveSubjectInsightPort.approve(param.getAssessmentId(), param.getSubjectId(), LocalDateTime.now());
    }
}
