package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.DeleteQuestionnaireUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteQuestionnaireRestController {

    private final DeleteQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}/questionnaires/{questionnaireId}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable("kitVersionId") Long kitVersionId,
                                                    @PathVariable("questionnaireId") Long questionnaireId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteQuestionnaire(toParam(kitVersionId, questionnaireId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteQuestionnaireUseCase.Param toParam(Long kitVersionId, Long questionnaireId, UUID currentUserId) {
        return new DeleteQuestionnaireUseCase.Param(kitVersionId, questionnaireId, currentUserId);
    }
}
