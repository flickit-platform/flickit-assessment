package org.flickit.assessment.advice.application.service.advicenarration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.ApproveAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ADVICE_NARRATION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAdviceNarrationService implements ApproveAdviceNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Override
    public void approve(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ADVICE_NARRATION))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAdviceNarrationPort.approve(param.getAssessmentId());
    }
}
