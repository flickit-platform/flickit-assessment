package org.flickit.assessment.advice.adapter.in.rest.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advice.GenerateAdvicePlanUseCase;
import org.flickit.assessment.advice.application.port.in.advice.GenerateAdvicePlanUseCase.Param;
import org.flickit.assessment.advice.application.port.in.advice.GenerateAdvicePlanUseCase.Result;
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
public class GenerateAdvicePlanRestController {

    private final UserContext userContext;
    private final GenerateAdvicePlanUseCase useCase;

    @PostMapping("/assessments/{assessmentId}/advice")
    ResponseEntity<GenerateAdvicePlanResponseDto> generate(@PathVariable("assessmentId") UUID assessmentId,
                                                           @RequestBody GenerateAdvicePlanRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        Param param = toParam(assessmentId, requestDto, currentUserId);
        Result result = useCase.generate(param);
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, GenerateAdvicePlanRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.attributeLevelTargets(), currentUserId);
    }

    private GenerateAdvicePlanResponseDto toResponseDto(Result result) {
        return new GenerateAdvicePlanResponseDto(result.adviceItems());
    }
}
