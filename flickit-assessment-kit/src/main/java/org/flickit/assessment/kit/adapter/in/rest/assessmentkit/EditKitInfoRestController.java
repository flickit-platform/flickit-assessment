package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EditKitInfoRestController {

    private final EditKitInfoUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/update/{kitId}")
    public ResponseEntity<EditKitInfoResponseDto> editKitInfo(@PathVariable("kitId") Long kitId,
                                                              @RequestBody EditKitInfoRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        EditKitInfoUseCase.Result result = useCase.editKitInfo(toParam(kitId, request, currentUserId));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private EditKitInfoUseCase.Param toParam(Long kitId, EditKitInfoRequestDto request, UUID currentUserId) {
        return new EditKitInfoUseCase.Param(
            kitId,
            request.data().title(),
            request.data().summary(),
            request.data().isActive(),
            request.data().isPrivate(),
            request.data().price(),
            request.data().about(),
            request.data().tags(),
            currentUserId
        );
    }

    private EditKitInfoResponseDto toResponse(EditKitInfoUseCase.Result result) {
        return new EditKitInfoResponseDto(
            result.title(),
            result.summary(),
            result.isActive(),
            result.isPrivate(),
            result.price(),
            result.about(),
            result.tags()
        );
    }
}
