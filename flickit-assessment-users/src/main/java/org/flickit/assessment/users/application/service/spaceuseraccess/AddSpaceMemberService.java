package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.AddSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE;

@Service
@Transactional
@RequiredArgsConstructor
public class AddSpaceMemberService implements AddSpaceMemberUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadUserPort loadUserPort;
    private final AddSpaceMemberPort addSpaceMemberPort;

    @Override
    public void addMember(Param param) {
        UUID currentUserId = param.getCurrentUserId();
        long spaceId = param.getSpaceId();

        boolean inviterHasAccess = checkSpaceAccessPort.checkIsMember(spaceId, currentUserId);
        if (!inviterHasAccess)
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        UUID userId = loadUserPort.loadUserIdByEmail(param.getEmail());

        boolean inviteeHasAccess = checkSpaceAccessPort.checkIsMember(spaceId, userId);
        if (inviteeHasAccess)
            throw new ResourceAlreadyExistsException(ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE);

        addSpaceMemberPort.persist(toParam(spaceId, userId, currentUserId, LocalDateTime.now()));
    }

    AddSpaceMemberPort.Param toParam(long spaceId, UUID userId, UUID currentUserId, LocalDateTime inviteTime) {
        return new AddSpaceMemberPort.Param(spaceId, userId, currentUserId, inviteTime);
    }
}
