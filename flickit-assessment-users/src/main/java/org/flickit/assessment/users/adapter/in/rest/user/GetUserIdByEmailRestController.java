package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.GetUserIdByEmailUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetUserIdByEmailRestController {

    private final GetUserIdByEmailUseCase useCase;

    @GetMapping("/users/emails/{email}")
    public ResponseEntity<GetUserIdByEmailResponseDto> getUserIdByEmail(@PathVariable("email") String email) {
        var result = useCase.getUserIdByEmail(toParam(email));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private GetUserIdByEmailUseCase.Param toParam(String email) {
        return new GetUserIdByEmailUseCase.Param(email);
    }

    private GetUserIdByEmailResponseDto toResponseDto(UUID id) {
        return new GetUserIdByEmailResponseDto(id);
    }
}
