package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddSpaceMemberRestController {

    private final UserContext userContext;
    private final AddSpaceMemberUseCase useCase;

    @PostMapping("/spaces/{id}/members")
    public ResponseEntity<Void> addSpaceMember(@PathVariable("id") Long id,
                                               @RequestBody AddSpaceMemberRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        useCase.addMember(toParam(id, request, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private AddSpaceMemberUseCase.Param toParam(Long id, AddSpaceMemberRequestDto request, UUID currentUserId) {
        return new AddSpaceMemberUseCase.Param(id, request.email(), currentUserId);
    }
}
