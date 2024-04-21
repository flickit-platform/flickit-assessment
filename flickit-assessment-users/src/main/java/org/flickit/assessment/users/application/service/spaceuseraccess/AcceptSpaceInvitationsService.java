package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.flickit.assessment.users.common.ErrorMessageKey.ACCEPT_SPACE_INVITATIONS_USER_ID_EMAIL_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptSpaceInvitationsService implements AcceptSpaceInvitationsUseCase {

    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;

    @Override
    public void acceptInvitations(Param param) {
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        if (!Objects.equals(email, param.getEmail()))
            throw new ResourceNotFoundException(ACCEPT_SPACE_INVITATIONS_USER_ID_EMAIL_NOT_FOUND);
    }
}
