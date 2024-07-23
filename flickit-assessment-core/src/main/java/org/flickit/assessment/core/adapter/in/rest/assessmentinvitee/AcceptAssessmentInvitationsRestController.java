package org.flickit.assessment.core.adapter.in.rest.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AcceptAssessmentInvitationsRestController {

    private final AcceptAssessmentInvitationsUseCase useCase;

    @PutMapping("/assessments-accept-invitations")
    public ResponseEntity<Void> acceptAssessmentInvitations(@RequestBody AcceptAssessmentInvitationsRequestDto requestDto) {
        useCase.acceptInvitations(toParam(requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(AcceptAssessmentInvitationsRequestDto requestDto) {
        return new Param(requestDto.userId());
    }
}
