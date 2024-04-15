package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceaccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceaccess.AddSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.spaceaccess.CheckMemberSpaceAccessPort;
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
    private final CheckMemberSpaceAccessPort checkMemberSpaceAccessPort;
    private final LoadUserIdByEmailPort loadUserIdByEmailPort;
    private final AddSpaceMemberPort addSpaceMemberPort;

    @Override
    public void addMember(Param param) {
        var currentUserId = param.getCurrentUserId();
        if (!checkSpaceExistencePort.existsById(param.getSpaceId()))
            throw new ValidationException(ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND);

        boolean inviterHasAccess = checkMemberSpaceAccessPort.checkAccess(currentUserId);
        if(!inviterHasAccess)
            throw new AccessDeniedException(ADD_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND);

        UUID userId = loadUserIdByEmailPort.loadByEmail(param.getEmail());

        boolean inviteeHasAccess = checkMemberSpaceAccessPort.checkAccess(userId);
        if (inviteeHasAccess)
            throw new ResourceAlreadyExistsException(ADD_SPACE_MEMBER_INVITEE_ACCESS_FOUND);

        addSpaceMemberPort.addMemberAccess(toParam(param.getSpaceId(), userId, currentUserId, LocalDateTime.now()));
    }

    AddSpaceMemberPort.Param toParam(long spaceId, UUID userId, UUID currentUserId, LocalDateTime inviteTime){
        return new AddSpaceMemberPort.Param(spaceId, userId, currentUserId, inviteTime);
    }
}
