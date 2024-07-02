package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceUseCase;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LeaveSpaceRestController {

    private final LeaveSpaceUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/spaces/{id}/leave")
    public ResponseEntity<Void> leaveSpace(@PathVariable("id") long id) {
        var currentUserId = userContext.getUser().id();
        useCase.leaveSpace(toParam(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(long spaceId, UUID currentUserId) {
        return new Param(spaceId, currentUserId);
    }
}
