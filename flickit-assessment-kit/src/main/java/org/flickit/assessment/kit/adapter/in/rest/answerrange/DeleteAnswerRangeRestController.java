package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answerrange.DeleteAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.in.answerrange.DeleteAnswerRangeUseCase.Param;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAnswerRangeRestController {

    private final UserContext userContext;
    private final DeleteAnswerRangeUseCase useCase;

    @DeleteMapping("/kit-versions/{kitVersionId}/answer-ranges/{answerRangeId}")
    public ResponseEntity<Void> deleteAnswerRange(@PathVariable("kitVersionId") Long kitVersionId,
                                                  @PathVariable("answerRangeId") Long answerRangeId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAnswerRange(toParam(kitVersionId, answerRangeId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static Param toParam(Long kitVersionId, Long answerRangeId, UUID currentUserId) {
        return new Param(answerRangeId,
            kitVersionId,
            currentUserId);
    }
}
