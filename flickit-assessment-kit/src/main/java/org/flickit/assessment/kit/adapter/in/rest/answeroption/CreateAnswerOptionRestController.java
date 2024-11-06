package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Param;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAnswerOptionRestController {

    private final CreateAnswerOptionUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/answer-options")
    public ResponseEntity<CreateAnswerOptionResponseDto> createAnswerOption(@PathVariable("kitVersionId") Long kitVersionId,
                                                                            @RequestBody CreateAnswerOptionRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.createAnswerOption(toParam(kitVersionId, requestDto, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId, CreateAnswerOptionRequestDto requestDto, UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.questionId(),
            requestDto.index(),
            requestDto.title(),
            requestDto.value(),
            currentUserId);
    }

    private CreateAnswerOptionResponseDto toResponse(Result result) {
        return new CreateAnswerOptionResponseDto(result.id(), result.answerRangeId());
    }
}
