package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceAiNarrationUseCase;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceAiNarrationUseCase.*;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAdviceAiNarrationRestController {

    private final CreateAdviceAiNarrationUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessments/{assessmentId}/advice-narration-ai")
    ResponseEntity<Void> createAdviceAiNarration(@PathVariable("assessmentId") UUID assessmentId,
                                                 @RequestBody CreateAdviceAiNarrationRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.createAdviceAiNarration(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAdviceAiNarrationRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.adviceListItems(), requestDto.attributeLevelTargets(), currentUserId);
    }
}
