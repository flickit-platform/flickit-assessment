package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.spaceinvitee.LoadSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptSpaceInvitationsService implements AcceptSpaceInvitationsUseCase {

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final LoadSpaceUserInvitationsPort loadSpaceUserInvitationsPort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final DeleteSpaceUserInvitationsPort deleteSpaceUserInvitationsPort;

    @Override
    public void acceptInvitations(Param param) {
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        var invitations = loadSpaceUserInvitationsPort.loadInvitations(email);

        List<CreateSpaceUserAccessPort.Param> validInvitations = new ArrayList<>();
        if (invitations != null) {
            validInvitations = invitations.stream()
                .filter(i -> i.expirationDate().isAfter(LocalDateTime.now()))
                .map(i -> toUserAccessPortParam(i, param.getUserId())).toList();

            deleteSpaceUserInvitationsPort.delete(email);
        }

        if (!validInvitations.isEmpty())
            createSpaceUserAccessPort.createAccess(validInvitations);
    }

    private CreateSpaceUserAccessPort.Param toUserAccessPortParam(LoadSpaceUserInvitationsPort.Invitation invitation, UUID userId) {
        return new CreateSpaceUserAccessPort.Param(
            invitation.spaceId(),
            userId,
            LocalDateTime.now(),
            invitation.createdBy());
    }
}
