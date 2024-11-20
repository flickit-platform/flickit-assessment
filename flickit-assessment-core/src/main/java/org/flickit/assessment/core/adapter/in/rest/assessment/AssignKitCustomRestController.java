package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.AssignKitCustomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AssignKitCustomRestController {

    private final AssignKitCustomUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/assign-kit-custom")
    public ResponseEntity<Void> assignKitCustom(@PathVariable("assessmentId")UUID assessmentId,
                                                @RequestBody AssignKitCustomRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.assignKitCustom(toParam(assessmentId, requestDto.kitCustomId(), currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static AssignKitCustomUseCase.Param toParam(UUID assessmentId,
                                                        Long kitCustomId,
                                                        UUID currentUserId) {
        return new AssignKitCustomUseCase.Param(assessmentId, kitCustomId, currentUserId);
    }
}
