package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class CreateExpertGroupRestController {

    private final CreateExpertGroupUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups")
    public ResponseEntity<CreateExpertGroupResponseDto> createExpertGroup(@RequestBody CreateExpertGroupRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        CreateExpertGroupResponseDto response = toResponseDto(useCase.createExpertGroup(toParam(request, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(CreateExpertGroupRequestDto requestDto, UUID currentUserId) {
        return new CreateExpertGroupUseCase.Param(
            requestDto.name(),
            requestDto.bio(),
            requestDto.about(),
            requestDto.website(),
            requestDto.picture(),
            currentUserId
        );
    }

    private CreateExpertGroupResponseDto toResponseDto(CreateExpertGroupUseCase.Result result) {
        return new CreateExpertGroupResponseDto(result.id());
    }
}
