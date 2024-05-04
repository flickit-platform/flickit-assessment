package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase.Param;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSpaceRestController {

    private final UpdateSpaceUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/spaces")
    public ResponseEntity<UpdateSpaceResponseDto> updateSpace(
        @PathVariable long id,
        @RequestBody UpdateSpaceRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        UpdateSpaceResponseDto response =
            toResponseDto(useCase.updateSpace(toParam(id, request, currentUserId)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(long id, UpdateSpaceRequestDto request, UUID currentUserId) {
        return new Param(
            id,
            request.title(),
            currentUserId
        );
    }

    private UpdateSpaceResponseDto toResponseDto(UpdateSpaceUseCase.Result result) {
        return new UpdateSpaceResponseDto(result.id());
    }
}
