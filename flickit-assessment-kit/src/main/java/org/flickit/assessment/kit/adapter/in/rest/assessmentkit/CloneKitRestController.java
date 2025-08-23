package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CloneKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CloneKitRestController {

    private final CloneKitUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/{kitId}/clone")
    public ResponseEntity<CloneKitResponseDto> cloneKit(@PathVariable("kitId") Long kitId) {
        UUID currentUserId = userContext.getUser().id();
        long kitVersionId = useCase.cloneKitUseCase(toParam(kitId, currentUserId));
        return new ResponseEntity<>(new CloneKitResponseDto(kitVersionId), HttpStatus.CREATED);
    }

    private CloneKitUseCase.Param toParam(Long kitId, UUID currentUserId) {
        return new CloneKitUseCase.Param(kitId, currentUserId);
    }
}
