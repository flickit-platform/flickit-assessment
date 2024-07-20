package org.flickit.assessment.core.application.service.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptAssessmentInvitationsService implements AcceptAssessmentInvitationsUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public void acceptInvitations(Param param) {
        var user = loadUserPort.loadById(param.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_FOUND));


    }
}
