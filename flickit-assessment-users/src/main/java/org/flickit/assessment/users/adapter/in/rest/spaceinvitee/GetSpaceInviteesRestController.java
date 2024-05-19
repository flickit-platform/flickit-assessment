package org.flickit.assessment.users.adapter.in.rest.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceInviteesUseCase;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceInviteesUseCase.Invitee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSpaceInviteesRestController {

    private final GetSpaceInviteesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/spaces/{id}/invitees")
    public ResponseEntity<PaginatedResponse<Invitee>> getSpaceInvitees(@PathVariable("id") long id,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        var invitees = useCase.getInvitees(toParam(id, currentUserId, size, page));

        return new ResponseEntity<>(invitees, HttpStatus.OK);
    }

    private GetSpaceInviteesUseCase.Param toParam(Long id, UUID currentUserId, int size, int page) {
        return new GetSpaceInviteesUseCase.Param(id, currentUserId, size, page);
    }
}
