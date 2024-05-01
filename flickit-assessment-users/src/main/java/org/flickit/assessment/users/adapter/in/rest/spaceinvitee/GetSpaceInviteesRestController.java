package org.flickit.assessment.users.adapter.in.rest.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceUserInvitationsPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSpaceInviteesRestController {

    private final UserContext userContext;
    private final GetSpaceUserInvitationsPort.GetSpaceInviteesUseCase useCase;

    @GetMapping("/spaces/{id}/invitees")
    public ResponseEntity<Void> getSpaceInvitees(
        @PathVariable("id") long id,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        useCase.getInvitees(toParam(id, currentUserId, size, page));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private GetSpaceUserInvitationsPort.GetSpaceInviteesUseCase.Param toParam(Long id, UUID currentUserId, int size, int page) {
        return new GetSpaceUserInvitationsPort.GetSpaceInviteesUseCase.Param(id, currentUserId, size, page);
    }
}
