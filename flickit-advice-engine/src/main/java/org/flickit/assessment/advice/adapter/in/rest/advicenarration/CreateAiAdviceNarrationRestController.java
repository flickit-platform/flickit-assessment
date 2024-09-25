package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase.Param;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase.Result;
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
public class CreateAiAdviceNarrationRestController {

    private final CreateAiAdviceNarrationUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessments/{assessmentId}/advice-narration-ai")
    ResponseEntity<Result> createAiAdviceNarration(@PathVariable("assessmentId") UUID assessmentId,
                                                   @RequestBody CreateAiAdviceNarrationRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.createAiAdviceNarration(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAiAdviceNarrationRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.adviceListItems(), requestDto.attributeLevelTargets(), currentUserId);
    }
}
