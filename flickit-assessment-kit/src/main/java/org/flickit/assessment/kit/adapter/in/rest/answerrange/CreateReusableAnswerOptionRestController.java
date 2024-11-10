package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateReusableAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateReusableAnswerOptionUseCase.Param;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateReusableAnswerOptionUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateReusableAnswerOptionRestController {

    private final CreateReusableAnswerOptionUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/answer-range-options")
    public ResponseEntity<CreateReusableAnswerOptionResponseDto> createReusableAnswerOption(@PathVariable("kitVersionId") Long kitVersionId,
                                                                                            @RequestBody CreateReusableAnswerOptionRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.createReusableAnswerOption(toParam(kitVersionId, requestDto, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId, CreateReusableAnswerOptionRequestDto requestDto, UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.answerRangeId(),
            requestDto.index(),
            requestDto.title(),
            requestDto.value(),
            currentUserId);
    }

    private CreateReusableAnswerOptionResponseDto toResponse(Result result) {
        return new CreateReusableAnswerOptionResponseDto(result.id());
    }
}
