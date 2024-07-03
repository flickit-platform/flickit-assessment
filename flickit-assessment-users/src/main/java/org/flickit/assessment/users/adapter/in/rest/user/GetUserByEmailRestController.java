package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.GetUserByEmailUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetUserByEmailRestController {

    private final GetUserByEmailUseCase useCase;

    @GetMapping("/users/{email}")
    public ResponseEntity<GetUserByEmailResponseDto> getUserByEmail(@PathVariable("email") String email) {
        var result = useCase.getUserByEmail(toParam(email));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private GetUserByEmailUseCase.Param toParam(String email) {
        return new GetUserByEmailUseCase.Param(email);
    }

    private GetUserByEmailResponseDto toResponseDto(GetUserByEmailUseCase.Result result) {
        return new GetUserByEmailResponseDto(result.user().getId(),
            result.user().getEmail(),
            result.user().getDisplayName(),
            result.user().getBio(),
            result.user().getLinkedin(),
            result.user().getPicturePath(),
            result.lastLogin(),
            result.isSuperUser(),
            result.isStaff(),
            result.isActive(),
            result.password());
    }
}
