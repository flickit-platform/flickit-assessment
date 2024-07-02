package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteSpaceRestController {

    private final DeleteSpaceUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/spaces/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable("id") Long id) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteSpace(toParam(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(long id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }
}
