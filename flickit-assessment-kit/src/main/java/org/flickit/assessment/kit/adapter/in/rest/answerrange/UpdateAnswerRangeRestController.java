package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answerrange.UpdateAnswerRangeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAnswerRangeRestController {

    private final UserContext userContext;
    private final UpdateAnswerRangeUseCase useCase;

    @PutMapping("/kit-versions/{kitVersionId}/answer-ranges/{answerRangeId}")
    public ResponseEntity<Void> updateAnswerRange(@PathVariable("kitVersionId") Long kitVersionId,
                                                  @PathVariable("answerRangeId") Long answerRangeId,
                                                  @RequestBody UpdateAnswerRangeRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAnswerRange(toParam(kitVersionId, answerRangeId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static UpdateAnswerRangeUseCase.Param toParam(Long kitVersionId,
                                                          Long answerRangeId,
                                                          UUID currentUserId,
                                                          UpdateAnswerRangeRequestDto requestDto) {
        return new UpdateAnswerRangeUseCase.Param(kitVersionId,
            answerRangeId,
            requestDto.title(),
            requestDto.reusable(),
            currentUserId);
    }
}
