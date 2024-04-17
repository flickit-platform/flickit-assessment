package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.AddSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceMemberAccessPort;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistencePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AddSpaceMemberService implements AddSpaceMemberUseCase {

    private final CheckSpaceExistencePort checkSpaceExistencePort;
    private final CheckSpaceMemberAccessPort checkSpaceMemberAccessPort;
    private final LoadUserIdByEmailPort loadUserIdByEmailPort;
    private final AddSpaceMemberPort addSpaceMemberPort;

    @Override
    public void addMember(Param param) {
        var currentUserId = param.getCurrentUserId();
        var spaceId = param.getSpaceId();
        if (!checkSpaceExistencePort.existsById(spaceId))
            throw new ValidationException(ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND);

        boolean inviterHasAccess = checkSpaceMemberAccessPort.checkIsMember(spaceId, currentUserId);
        if (!inviterHasAccess)
            throw new AccessDeniedException(ADD_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND);

        UUID userId = loadUserIdByEmailPort.loadByEmail(param.getEmail());

        boolean inviteeHasAccess = checkSpaceMemberAccessPort.checkIsMember(spaceId, userId);
        if (inviteeHasAccess)
            throw new ResourceAlreadyExistsException(ADD_SPACE_MEMBER_INVITEE_ACCESS_FOUND);

        addSpaceMemberPort.persist(toParam(spaceId, userId, currentUserId, LocalDateTime.now()));
    }

    AddSpaceMemberPort.Param toParam(long spaceId, UUID userId, UUID currentUserId, LocalDateTime inviteTime) {
        return new AddSpaceMemberPort.Param(spaceId, userId, currentUserId, inviteTime);
    }
}
