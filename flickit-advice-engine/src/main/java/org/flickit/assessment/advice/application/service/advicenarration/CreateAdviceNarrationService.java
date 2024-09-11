package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceNarrationUseCase;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class CreateAdviceNarrationService implements CreateAdviceNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void createAdviceNarration(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
