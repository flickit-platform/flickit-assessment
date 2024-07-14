package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfilePictureUseCase;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfilePictureUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateUserProfilePictureRestController {

    private final UpdateUserProfilePictureUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/users/picture")
    public ResponseEntity<UpdateUserProfilePictureResponseDto> updateUserProfilePicture(@RequestParam MultipartFile pictureFile) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponseDto(useCase.updateUserProfilePicture(toParam(currentUserId, pictureFile)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(UUID userId, MultipartFile pictureFile) {
        return new Param(userId, pictureFile);
    }

    private UpdateUserProfilePictureResponseDto toResponseDto(Result result) {
        return new UpdateUserProfilePictureResponseDto(result.pictureLink());
    }
}
