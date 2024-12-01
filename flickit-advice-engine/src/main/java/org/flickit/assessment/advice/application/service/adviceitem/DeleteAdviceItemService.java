package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAdviceItemService implements DeleteAdviceItemUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final DeleteAdviceItemPort deleteAdviceItemPort;

    @Override
    public void deleteAdviceItem(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        deleteAdviceItemPort.deleteAdviceItem(param.getAdviceItemId());
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
