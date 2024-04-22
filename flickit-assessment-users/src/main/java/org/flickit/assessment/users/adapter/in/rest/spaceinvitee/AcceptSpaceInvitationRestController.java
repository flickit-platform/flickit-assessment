package org.flickit.assessment.users.adapter.in.rest.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AcceptSpaceInvitationRestController {

    private final AcceptSpaceInvitationsUseCase useCase;

    @PutMapping("/spaces/new-users")
    public ResponseEntity<Void> inviteExpertGroupMember(
        @RequestBody AcceptSpaceInvitationRequestDto requestDto) {
        useCase.acceptInvitations(toParam(requestDto));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private AcceptSpaceInvitationsUseCase.Param toParam(AcceptSpaceInvitationRequestDto requestDto) {
        return new AcceptSpaceInvitationsUseCase.Param(requestDto.userId(), requestDto.email());
    }
}
