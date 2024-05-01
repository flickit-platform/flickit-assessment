package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteAssessmentKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAssessmentKitRestController {

    private final DeleteAssessmentKitUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/assessment-kits/{kitId}")
    public ResponseEntity<Void> deleteAssessmentKit(@PathVariable("kitId") Long kitId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.delete(toParam(kitId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteAssessmentKitUseCase.Param toParam(Long kitId, UUID currentUserId) {
        return new DeleteAssessmentKitUseCase.Param(kitId, currentUserId);
    }
}
