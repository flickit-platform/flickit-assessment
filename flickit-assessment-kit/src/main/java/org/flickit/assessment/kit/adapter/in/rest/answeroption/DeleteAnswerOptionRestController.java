package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answeroption.DeleteAnswerOptionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAnswerOptionRestController {

    private final DeleteAnswerOptionUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}/answer-options/{answerOptionId}")
    public ResponseEntity<Void> deleteAnswerOption(@PathVariable Long kitVersionId, @PathVariable Long answerOptionId) {
        var currentUserId = userContext.getUser().id();
        useCase.delete(toParam(kitVersionId, answerOptionId, currentUserId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteAnswerOptionUseCase.Param toParam(Long kitVersionId, Long answerOptionId, UUID currentUserId) {
        return new DeleteAnswerOptionUseCase.Param(answerOptionId, kitVersionId, currentUserId);
    }
}
