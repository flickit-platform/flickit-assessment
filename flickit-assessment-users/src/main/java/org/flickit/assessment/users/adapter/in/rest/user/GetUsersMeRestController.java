package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.GetUsersMeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetUsersMeRestController {

    private final GetUsersMeUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/users/me")
    public ResponseEntity<GetUsersMeResponseDto> getUsersMe() {
        UUID currentUserId = userContext.getUser().id();
        GetUsersMeUseCase.UserProfile user = useCase.getUserProfile(new GetUsersMeUseCase.Param(currentUserId));
        return new ResponseEntity<>(toResponseDto(user), HttpStatus.OK);
    }

    private GetUsersMeResponseDto toResponseDto(GetUsersMeUseCase.UserProfile user) {
        return new GetUsersMeResponseDto(user.id(),
            user.displayName(),
            user.pictureLink(),
            user.defaultSpaceId(),
            user.showSurvey());
    }
}
