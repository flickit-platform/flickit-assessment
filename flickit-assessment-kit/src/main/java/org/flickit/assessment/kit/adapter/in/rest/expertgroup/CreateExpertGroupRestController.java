package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateExpertGroupRestController {

    private final CreateExpertGroupUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups")
    public ResponseEntity<CreateExpertGroupResponseDto> createExpertGroup(
        @RequestParam("title") String title, @RequestParam("bio") String bio, @RequestParam("about") String about,
        @RequestParam("picture") MultipartFile picture, @RequestParam("website") String website) {

        UUID currentUserId = userContext.getUser().id();
        CreateExpertGroupResponseDto response =
            toResponseDto(useCase.createExpertGroup(toParam(title,bio,about,website,picture,currentUserId)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(String title, String bio,String about, String website, MultipartFile picture, UUID currentUserId) {

        return new CreateExpertGroupUseCase.Param(
            title,
            bio,
            about,
            website,
            picture,
            currentUserId
        );
    }

    private CreateExpertGroupResponseDto toResponseDto(CreateExpertGroupUseCase.Result result) {
        return new CreateExpertGroupResponseDto(result.id());
    }
}
