package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetUserProfileRestController {

    private final GetUserProfileUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/user-profile")
    public ResponseEntity<GetUserProfileResponseDto> getUserProfile() {
        UUID currentUserId = userContext.getUser().id();
        UserProfile userProfile = useCase.getUserProfile(new GetUserProfileUseCase.Param(currentUserId));
        return new ResponseEntity<>(toResponseDto(userProfile), HttpStatus.OK);
    }

    private GetUserProfileResponseDto toResponseDto(UserProfile userProfile) {
        return new GetUserProfileResponseDto(userProfile.id(),
            userProfile.email(),
            userProfile.displayName(),
            userProfile.bio(),
            userProfile.linkedin(),
            userProfile.pictureLink());
    }
}
