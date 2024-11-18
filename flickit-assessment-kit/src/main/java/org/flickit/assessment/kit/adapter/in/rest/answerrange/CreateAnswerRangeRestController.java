package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase.Param;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAnswerRangeRestController {

    private final UserContext userContext;
    private final CreateAnswerRangeUseCase useCase;

    @PostMapping("/kit-versions/{kitVersionId}/answer-ranges")
    public ResponseEntity<Result> crateAnswerRange(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @RequestBody CreateAnswerRangeRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.createAnswerRange(toParam(kitVersionId, currentUserId, requestDto));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private static Param toParam(Long kitVersionId, UUID currentUserId, CreateAnswerRangeRequestDto requestDto) {
        return new Param(kitVersionId, requestDto.title(), currentUserId);
    }
}
