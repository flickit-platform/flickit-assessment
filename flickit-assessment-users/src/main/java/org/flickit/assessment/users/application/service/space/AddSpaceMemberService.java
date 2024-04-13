package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.space.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.space.AddSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.space.CheckMemberSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_INVITEE_ACCESS_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND;
@Service
@Transactional
@RequiredArgsConstructor
public class AddSpaceMemberService implements AddSpaceMemberUseCase {

    private final LoadSpacePort loadSpacePort;
    private final CheckMemberSpaceAccessPort checkMemberSpaceAccessPort;
    private final LoadUserIdByEmailPort loadUserIdByEmailPort;
    private final AddSpaceMemberPort addSpaceMemberPort;

    @Override
    public void addMember(long spaceId, String email, UUID currentUserId) {
        var space = loadSpacePort.getById(spaceId);
        if (space == null)
            throw new ResourceNotFoundException(ADD_SPACE_MEMBER_SPACE_ID_NOT_FOUND);

        boolean inviterHasAccess = checkMemberSpaceAccessPort.checkAccess(currentUserId);
        if(!inviterHasAccess)
            throw new ValidationException(ADD_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND);

        UUID userId = loadUserIdByEmailPort.loadByEmail(email);

        boolean inviteeHasAccess = checkMemberSpaceAccessPort.checkAccess(userId);
        if (inviteeHasAccess)
            throw new ResourceAlreadyExistsException(ADD_SPACE_MEMBER_INVITEE_ACCESS_FOUND);

        addSpaceMemberPort.addMemberAccess(spaceId, userId, currentUserId, LocalDateTime.now());

    }
}
