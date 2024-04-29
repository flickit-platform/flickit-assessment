package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistsPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSpaceService implements DeleteSpaceUseCase {

    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final CheckSpaceExistsPort checkSpaceExistsPort;

    @Override
    public void deleteSpace(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());

        if(!checkSpaceExistsPort.existsById(param.getId()))
            throw new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND);
    }

    private void validateCurrentUser(Long spaceId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadSpaceOwnerPort.loadOwnerId(spaceId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
