package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateExpertGroupPictureRestController {

    private final UpdateExpertGroupPictureUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}/picture")
    public ResponseEntity<UpdateExpertGroupPictureResponseDto> updateExpertGroupPicture(
        @PathVariable("id") Long id,
        @ModelAttribute UpdateExpertGroupPictureRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        UpdateExpertGroupPictureResponseDto response =
            toResponseDto(useCase.update(toParam(id, request, currentUserId)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(Long id, UpdateExpertGroupPictureRequestDto request, UUID currentUserId) {
        return new Param(
            id,
            request.picture(),
            currentUserId
        );
    }

    private UpdateExpertGroupPictureResponseDto toResponseDto(UpdateExpertGroupPictureUseCase.Result result) {
        return new UpdateExpertGroupPictureResponseDto(result.pictureLink());
    }
}
