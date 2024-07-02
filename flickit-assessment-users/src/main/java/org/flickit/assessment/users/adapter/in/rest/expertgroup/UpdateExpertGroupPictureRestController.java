package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateExpertGroupPictureRestController {

    private final UpdateExpertGroupPictureUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}/picture")
    public ResponseEntity<UpdateExpertGroupPictureResponseDto> updateExpertGroupPicture(
        @PathVariable("id") Long id,
        @RequestParam MultipartFile pictureFile) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponseDto(useCase.update(toParam(id, pictureFile, currentUserId)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(Long id, MultipartFile pictureFile, UUID currentUserId) {
        return new Param(id, pictureFile, currentUserId);
    }

    private UpdateExpertGroupPictureResponseDto toResponseDto(UpdateExpertGroupPictureUseCase.Result result) {
        return new UpdateExpertGroupPictureResponseDto(result.pictureLink());
    }
}
