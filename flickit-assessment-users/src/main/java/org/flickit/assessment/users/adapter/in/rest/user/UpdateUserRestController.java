package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.UpdateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateUserRestController {

    private final UpdateUserUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/users")
    public ResponseEntity<Void> updateUser(@RequestBody UpdateUserRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateUser(toParam(currentUserId, request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateUserUseCase.Param toParam(UUID currentUserId, UpdateUserRequestDto request) {
        return new UpdateUserUseCase.Param(currentUserId, request.displayName(), request.bio(), request.linkedin());
    }
}
