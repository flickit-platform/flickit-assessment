package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.ApproveAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.in.advicenarration.ApproveAdviceNarrationUseCase.Param;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApproveAdviceNarrationRestController {

    private final ApproveAdviceNarrationUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/approve-advice-narration")
    ResponseEntity<Void> approveAdviceNarration(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        useCase.approveAdviceNarration(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
