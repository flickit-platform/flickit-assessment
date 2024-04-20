package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceaccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceMemberAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceExistencePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SaveSpaceMemberInviteePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SendInviteMailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteSpaceMemberService implements InviteSpaceMemberUseCase {

    private final CheckSpaceExistencePort checkSpaceExistencePort;
    private final CheckSpaceMemberAccessPort checkSpaceMemberAccessPort;
    private final LoadUserIdByEmailPort loadUserIdByEmailPort;
    private final SaveSpaceMemberInviteePort saveSpaceMemberInviteePort;
    private final SendInviteMailPort sendInviteMailPort;

    @Override
    public void inviteMember(Param param) {
        var currentUserId = param.getCurrentUserId();
        if (!checkSpaceExistencePort.existsById(param.getSpaceId()))
            throw new ValidationException(INVITE_SPACE_MEMBER_SPACE_ID_NOT_FOUND);

        if (!checkSpaceMemberAccessPort.checkIsMember(currentUserId))
            throw new AccessDeniedException(INVITE_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND);

        if (loadUserIdByEmailPort.loadByEmail(param.getEmail()) != null)
            throw new ResourceAlreadyExistsException(INVITE_SPACE_MEMBER_INVITER_ACCESS_NOT_FOUND);

        saveSpaceMemberInviteePort.persist(toParam(param.getSpaceId(), param.getEmail(), currentUserId, LocalDateTime.now()));
        sendInviteMailPort.sendInviteMail(param.getEmail());
    }

    private SaveSpaceMemberInviteePort.Param toParam(Long spaceId, String email, UUID currentUserId, LocalDateTime now) {
        return new SaveSpaceMemberInviteePort.Param(spaceId, email, currentUserId, now);
    }
}
