package org.flickit.assessment.advice.adapter.in.rest.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase;
import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase.Param;
import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase.Result;
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
public class CreateAdviceRestController {

    private final UserContext userContext;
    private final CalculateAdviceUseCase useCase;

    @PostMapping("/assessments/{assessmentId}/advice")
    ResponseEntity<CalculateAdviceResponseDto> calculateAdvice(@PathVariable("assessmentId") UUID assessmentId,
                                                               @RequestBody CalculateAdviceRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        Param param = toParam(assessmentId, requestDto, currentUserId);
        Result result = useCase.calculateAdvice(param);
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, CalculateAdviceRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.attributeLevelTargets(), currentUserId);
    }

    private CalculateAdviceResponseDto toResponseDto(Result result) {
        return new CalculateAdviceResponseDto(result.adviceItems());
    }
}
