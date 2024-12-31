package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.subjectinsight.ApproveSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.subjectinsight.ApproveSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveSubjectInsightService implements ApproveSubjectInsightUseCase {

    private final ApproveSubjectInsightPort approveSubjectInsightPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void approveSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentPermission.APPROVE_SUBJECT_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveSubjectInsightPort.approve(param.getAssessmentId(), param.getSubjectId());
    }
}
