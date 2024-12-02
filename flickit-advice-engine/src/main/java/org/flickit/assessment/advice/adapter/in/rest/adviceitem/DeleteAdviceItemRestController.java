package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase.*;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAdviceItemRestController {

    private final UserContext userContext;
    private final DeleteAdviceItemUseCase useCase;

    @DeleteMapping("/assessments/{assessmentId}/advice-items/{adviceItemId}")
    ResponseEntity<Void> createAdviceItem(@PathVariable("assessmentId") UUID assessmentId,
                                          @PathVariable("adviceItemId") UUID adviceItemId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAdviceItem(toParam(assessmentId, adviceItemId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(UUID assessmentId, UUID adviceItemId, UUID currentUserId) {
        return new Param(adviceItemId,
            assessmentId,
            currentUserId);
    }
}