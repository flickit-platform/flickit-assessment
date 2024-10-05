package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.DeleteQuestionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteQuestionRestController {

    private final DeleteQuestionUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("kitVersionId") Long kitVersionId,
                                               @PathVariable("questionId") Long questionId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteQuestion(toParam(kitVersionId, questionId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteQuestionUseCase.Param toParam(Long kitVersionId, Long questionId, UUID currentUserId) {
        return new DeleteQuestionUseCase.Param(kitVersionId, questionId, currentUserId);
    }
}
