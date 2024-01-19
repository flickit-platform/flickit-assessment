package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class UpdateExpertGroupController {

    private final UpdateExpertGroupUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}")
    public ResponseEntity<Void> updateExpertGroupList(@PathVariable long id,
                                                      @ModelAttribute UpdateExpertGroupRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.updateExpertGroup(toParam(id, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateExpertGroupUseCase.Param toParam(long id,
                                                   UpdateExpertGroupRequestDto request,
                                                   UUID currentUserId) {
        String website = (request.website() != null && !request.website().isEmpty()) ? request.website().strip() : null;
        MultipartFile picture = request.picture();
        String fileName;
        try {
            fileName = picture.getOriginalFilename();
            picture = !Objects.requireNonNull(fileName).isEmpty() ? picture : null;
        } catch (NullPointerException e) {
            picture = null;
        }

        return new UpdateExpertGroupUseCase.Param(
            id,
            request.title(),
            request.bio(),
            request.about(),
            picture,
            website,
            currentUserId
        );
    }
}
