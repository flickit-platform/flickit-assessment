package org.flickit.assessment.users.application.service.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.SpaceInvitee;
import org.flickit.assessment.users.application.port.in.spaceinvitee.DeleteSpaceInvitationUseCase;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceInvitationPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInvitationPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_INVITE_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSpaceInvitationService implements DeleteSpaceInvitationUseCase {

    private final LoadSpaceInvitationPort loadSpaceInvitationPort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final DeleteSpaceInvitationPort deleteSpaceUserInvitationsPort;

    @Override
    public void deleteInvitation(Param param) {
        SpaceInvitee invitation = loadSpaceInvitationPort.loadSpaceInvitation(param.getInviteId())
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_SPACE_INVITATION_INVITE_ID_NOT_FOUND));

        if (!checkSpaceAccessPort.checkIsMember(invitation.getSpaceId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteSpaceUserInvitationsPort.deleteSpaceInvitation(param.getInviteId());
    }
}
