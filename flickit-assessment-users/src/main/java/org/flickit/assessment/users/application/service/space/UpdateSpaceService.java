package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CheckDefaultSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.space.UpdateSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_SPACE_DEFAULT_SPACE_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSpaceService implements UpdateSpaceUseCase {

    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final CheckDefaultSpacePort checkDefaultSpacePort;
    private final UpdateSpacePort updateSpacePort;

    @Override
    public void updateSpace(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());

        if (checkDefaultSpacePort.checkIsDefault(param.getId()))
            throw new AccessDeniedException(UPDATE_SPACE_DEFAULT_SPACE_NOT_ALLOWED);

        var updateParam = new UpdateSpacePort.Param(param.getId(),
            param.getTitle(),
            generateSlugCode(param.getTitle()),
            LocalDateTime.now(),
            param.getCurrentUserId());
        updateSpacePort.updateSpace(updateParam);
    }

    private void validateCurrentUser(Long spaceId, UUID currentUserId) {
        UUID spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(spaceId);
        if (!Objects.equals(spaceOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
