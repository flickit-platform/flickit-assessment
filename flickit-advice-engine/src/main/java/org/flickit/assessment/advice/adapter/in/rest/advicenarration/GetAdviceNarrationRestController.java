package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.GetAdviceNarrationUseCase;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAdviceNarrationRestController {

    private final GetAdviceNarrationUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/advice-narration")
    public ResponseEntity<GetAdviceNarrationUseCase.Result> getAdviceNarration(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var adviceNarration = useCase.getAdviceNarration(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(adviceNarration, HttpStatus.OK);
    }

    private GetAdviceNarrationUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
    }
}
