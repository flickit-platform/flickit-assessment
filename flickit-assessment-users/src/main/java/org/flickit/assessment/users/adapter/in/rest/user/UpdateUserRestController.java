package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.UpdateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateUserRestController {

    private final UpdateUserUseCase useCase;

    @PutMapping("/users/{id}")
    public ResponseEntity<UpdateUserResponseDto> updateUser(@PathVariable("id")UUID id,
                                                            @RequestBody UpdateUserRequestDto request) {

        User user = useCase.updateUser(toParam(id, request));
        UpdateUserResponseDto responseDto = toResponse(user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    private UpdateUserResponseDto toResponse(User user) {
        return new UpdateUserResponseDto(user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getBio(),
            user.getLinkedin(),
            user.getPicture());
    }

    private UpdateUserUseCase.Param toParam(UUID id, UpdateUserRequestDto request) {
        return new UpdateUserUseCase.Param(id, request.displayName(), request.bio(), request.linkedin());
    }
}
