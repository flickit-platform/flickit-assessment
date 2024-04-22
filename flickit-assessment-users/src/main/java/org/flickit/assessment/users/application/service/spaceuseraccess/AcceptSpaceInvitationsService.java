package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.LoadUserInvitedSpacesPort;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitations;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.ACCEPT_SPACE_INVITATIONS_USER_ID_EMAIL_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptSpaceInvitationsService implements AcceptSpaceInvitationsUseCase {

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final LoadUserInvitedSpacesPort loadUserInvitedSpacesPort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final DeleteSpaceUserInvitations deleteSpaceUserInvitations;
    @Override
    public void acceptInvitations(Param param) {
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        if (!Objects.equals(email, param.getEmail()))
            throw new ResourceNotFoundException(ACCEPT_SPACE_INVITATIONS_USER_ID_EMAIL_NOT_FOUND);

        var portResult = loadUserInvitedSpacesPort.loadSpaces(param.getEmail());

        List<CreateSpaceUserAccessPort.Param> result;
        if (portResult != null){
            result = portResult.stream().map(s -> toUserAccessPortParam(s, param.getUserId())).toList();
            createSpaceUserAccessPort.createAccess(result);
            deleteSpaceUserInvitations.delete(email);
        }
    }

    private CreateSpaceUserAccessPort.Param toUserAccessPortParam(LoadUserInvitedSpacesPort.Result portResult, UUID userId) {
        return new CreateSpaceUserAccessPort.Param(
            portResult.spaceId(),
            userId,
            LocalDateTime.now(),
            portResult.createdBy());
    }
}
