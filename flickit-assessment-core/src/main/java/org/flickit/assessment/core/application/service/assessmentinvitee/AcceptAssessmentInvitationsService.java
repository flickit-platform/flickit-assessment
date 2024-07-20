package org.flickit.assessment.core.application.service.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptAssessmentInvitationsService implements AcceptAssessmentInvitationsUseCase {

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;

    @Override
    public void acceptInvitations(Param param) {
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

    }
}
