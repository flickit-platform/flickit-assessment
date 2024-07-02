package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfileUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateUserProfileRestController {

    private final UpdateUserProfileUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/users")
    public ResponseEntity<Void> updateUser(@RequestBody UpdateUserProfileRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateUserProfile(toParam(currentUserId, request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateUserProfileUseCase.Param toParam(UUID currentUserId, UpdateUserProfileRequestDto request) {
        return new UpdateUserProfileUseCase.Param(currentUserId, request.displayName(), request.bio(), request.linkedin());
    }
}
