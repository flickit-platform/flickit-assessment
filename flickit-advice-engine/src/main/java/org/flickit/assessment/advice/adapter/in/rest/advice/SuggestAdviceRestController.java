package org.flickit.assessment.advice.adapter.in.rest.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase.Param;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase.Result;
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
public class SuggestAdviceRestController {

    private final UserContext userContext;
    private final SuggestAdviceUseCase useCase;

    @PostMapping("/assessments/{assessmentId}/advice")
    ResponseEntity<SuggestAdviceResponseDto> suggestAdvice(
        @PathVariable("assessmentId") UUID assessmentId,
        @RequestBody SuggestAdviceRequestDto requestDto) {

        UUID currentUserId = userContext.getUser().id();
        Param param = toParam(assessmentId, requestDto, currentUserId);
        Result result = useCase.suggestAdvice(param);
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, SuggestAdviceRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.targets(), currentUserId);
    }

    private SuggestAdviceResponseDto toResponseDto(Result result) {
        return new SuggestAdviceResponseDto(result.questions());
    }
}
