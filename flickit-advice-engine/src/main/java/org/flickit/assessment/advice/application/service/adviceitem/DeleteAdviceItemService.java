package org.flickit.assessment.advice.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.DELETE_ADVICE_ITEM_ASSESSMENT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ADVICE_ITEM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAdviceItemService implements DeleteAdviceItemUseCase {

    private final LoadAdviceItemPort loadAdviceItemPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final DeleteAdviceItemPort deleteAdviceItemPort;

    @Override
    public void deleteAdviceItem(Param param) {
        var assessmentId = loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_ADVICE_ITEM_ASSESSMENT_NOT_FOUND));

        validateUserAccess(assessmentId, param.getCurrentUserId());

        deleteAdviceItemPort.delete(param.getAdviceItemId());
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, MANAGE_ADVICE_ITEM))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
