package org.flickit.assessment.users.application.service.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.DeleteSpaceInvitationUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceInvitationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSpaceInvitationService implements DeleteSpaceInvitationUseCase {

    private final LoadSpaceOwnerPort loadSpaceOwnerPort;

    private final DeleteSpaceInvitationPort deleteSpaceUserInvitationsPort;

    @Override
    public void deleteInvitation(Param param) {
        if (!loadSpaceOwnerPort.loadOwnerId(param.getSpaceId()).equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteSpaceUserInvitationsPort.deleteSpaceInvitation(param.getInviteId());

    }
}
