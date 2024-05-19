package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.SpaceInvitee;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        List<SpaceUserAccess> validInvitations = invitations.stream()
            .filter(SpaceInvitee::isNotExpired)
            .map(i -> toSpaceUserAccess(i, param.getUserId())).toList();

        if (!validInvitations.isEmpty())
            createSpaceUserAccessPort.persistAll(validInvitations);

        if (!invitations.isEmpty())
            deleteSpaceUserInvitationsPort.deleteAll(email);
    }

    private SpaceUserAccess toSpaceUserAccess(SpaceInvitee invitation, UUID userId) {
        return new SpaceUserAccess(
            invitation.getSpaceId(),
            userId,
            invitation.getInviterId(),
            LocalDateTime.now());
    }
}
