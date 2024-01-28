package org.flickit.assessment.advice.adapter.in.rest.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
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

    private final SuggestAdviceUseCase useCase;

    @PostMapping("/assessments/{assessmentId}/advice")
    ResponseEntity<SuggestAdviceUseCase.Result> suggestAdvice(
        @PathVariable("assessmentId") UUID assessmentId,
        @RequestBody SuggestAdviceRequestDto requestDto) {

        SuggestAdviceUseCase.Result result = useCase.suggestAdvice(toParam(assessmentId, requestDto));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private SuggestAdviceUseCase.Param toParam(UUID assessmentId, SuggestAdviceRequestDto requestDto) {
        return new SuggestAdviceUseCase.Param(assessmentId, requestDto.targets());
    }
}
