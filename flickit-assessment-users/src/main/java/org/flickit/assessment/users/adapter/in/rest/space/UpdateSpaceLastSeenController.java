package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceLastSeenUseCase;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceLastSeenUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSpaceLastSeenController {

    private final UpdateSpaceLastSeenUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/spaces/{id}/update-last-seen")
    public ResponseEntity<CreateSpaceResponseDto> updateSpaceLastSeen(@PathVariable("id") Long id) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateLastSeen(toParam(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }
}
