package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateExpertGroupRestController {

    private final CreateExpertGroupUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups")
    public ResponseEntity<CreateExpertGroupResponseDto> createExpertGroup(
        @ModelAttribute CreateExpertGroupRequestDto request) {

        UUID currentUserId = userContext.getUser().id();
        CreateExpertGroupResponseDto response =
            toResponseDto(useCase.createExpertGroup(toParam(request, currentUserId)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(CreateExpertGroupRequestDto request, UUID currentUserId) {
        String website = (request.website() != null) ? request.website().trim() : null;

        MultipartFile picture = request.picture();
        picture = (picture != null && picture.getOriginalFilename() != null && !picture.getOriginalFilename().isEmpty()) ? picture : null;

        return new CreateExpertGroupUseCase.Param(
            request.title(),
            request.bio(),
            request.about(),
            picture,
            website,
            currentUserId
        );
    }

    private CreateExpertGroupResponseDto toResponseDto(CreateExpertGroupUseCase.Result result) {
        return new CreateExpertGroupResponseDto(result.id());
    }
}
