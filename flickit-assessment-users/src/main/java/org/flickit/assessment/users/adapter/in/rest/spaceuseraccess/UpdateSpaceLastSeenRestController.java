package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.UpdateSpaceLastSeenUseCase;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.UpdateSpaceLastSeenUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSpaceLastSeenRestController {

    private final UpdateSpaceLastSeenUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/spaces/{id}/seen")
    public ResponseEntity<Void> updateSpaceLastSeen(@PathVariable("id") Long spaceId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateLastSeen(toParam(spaceId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long spaceId, UUID currentUserId) {
        return new Param(spaceId, currentUserId);
    }
}
