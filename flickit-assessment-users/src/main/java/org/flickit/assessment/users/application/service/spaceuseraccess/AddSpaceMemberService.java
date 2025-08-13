package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AddSpaceMemberService implements AddSpaceMemberUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpacePort loadSpacePort;
    private final LoadUserPort loadUserPort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Override
    public void addMember(Param param) {
        UUID currentUserId = param.getCurrentUserId();
        long spaceId = param.getSpaceId();

        validateSpace(spaceId, currentUserId);

        UUID userId = loadUserPort.loadUserIdByEmail(param.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException(USER_BY_EMAIL_NOT_FOUND));

        boolean inviteeHasAccess = checkSpaceAccessPort.checkIsMember(spaceId, userId);
        if (inviteeHasAccess)
            throw new ResourceAlreadyExistsException(ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE);

        var access = new SpaceUserAccess(spaceId, userId, currentUserId, LocalDateTime.now());
        createSpaceUserAccessPort.persist(access);
    }

    private void validateSpace(long spaceId, UUID currentUserId) {
        boolean inviterHasAccess = checkSpaceAccessPort.checkIsMember(spaceId, currentUserId);
        if (!inviterHasAccess)
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (loadSpacePort.checkIsDefault(spaceId))
            throw new ValidationException(ADD_SPACE_MEMBER_SPACE_DEFAULT_SPACE);
    }
}
