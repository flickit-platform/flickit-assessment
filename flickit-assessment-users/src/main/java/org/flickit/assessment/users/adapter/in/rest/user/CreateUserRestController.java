package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateUserRestController {

    private final CreateUserUseCase useCase;

    @PostMapping("/users")
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody CreateUserRequestDto request) {
        CreateUserUseCase.Result result = useCase.createUser(toParam(request));
        return new ResponseEntity<>(new CreateUserResponseDto(result.userId()), HttpStatus.CREATED);
    }

    private CreateUserUseCase.Param toParam(CreateUserRequestDto request) {
        return new CreateUserUseCase.Param(request.id(), request.email(), request.displayName());
    }
}
