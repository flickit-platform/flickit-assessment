package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpaceAssessmentPort;
import org.flickit.assessment.users.application.port.out.space.DeleteSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_ASSESSMENT_EXIST;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSpaceService implements DeleteSpaceUseCase {

    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final CountSpaceAssessmentPort countSpaceAssessmentPort;
    private final DeleteSpacePort deleteSpacePort;

    @Override
    public void deleteSpace(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());

        if (countSpaceAssessmentPort.countAssessments(param.getId()) > 0)
            throw new ValidationException(DELETE_SPACE_ASSESSMENT_EXIST);

        deleteSpacePort.deleteById(param.getId(), System.currentTimeMillis());
    }

    private void validateCurrentUser(Long spaceId, UUID currentUserId) {
        UUID spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(spaceId);
        if (!Objects.equals(spaceOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
