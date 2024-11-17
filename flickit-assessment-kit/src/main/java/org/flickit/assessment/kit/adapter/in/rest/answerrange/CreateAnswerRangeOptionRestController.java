package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeOptionUseCase;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeOptionUseCase.Param;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeOptionUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAnswerRangeOptionRestController {

    private final CreateAnswerRangeOptionUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/answer-range-options")
    public ResponseEntity<CreateAnswerRangeOptionResponseDto> createAnswerRangeOption(@PathVariable("kitVersionId") Long kitVersionId,
                                                                                      @RequestBody CreateAnswerRangeOptionRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.createAnswerRangeOption(toParam(kitVersionId, requestDto, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId, CreateAnswerRangeOptionRequestDto requestDto, UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.answerRangeId(),
            requestDto.index(),
            requestDto.title(),
            requestDto.value(),
            currentUserId);
    }

    private CreateAnswerRangeOptionResponseDto toResponse(Result result) {
        return new CreateAnswerRangeOptionResponseDto(result.id());
    }
}
