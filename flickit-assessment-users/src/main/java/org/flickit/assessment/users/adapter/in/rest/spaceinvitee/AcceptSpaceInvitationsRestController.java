package org.flickit.assessment.users.adapter.in.rest.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AcceptSpaceInvitationsRestController {

    private final AcceptSpaceInvitationsUseCase useCase;

    @PutMapping("/spaces-accept-invitations")
    public ResponseEntity<Void> acceptSpaceInvitations(@RequestBody AcceptSpaceInvitationsRequestDto requestDto) {
        useCase.acceptInvitations(toParam(requestDto));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(AcceptSpaceInvitationsRequestDto requestDto) {
        return new Param(requestDto.userId());
    }
}
