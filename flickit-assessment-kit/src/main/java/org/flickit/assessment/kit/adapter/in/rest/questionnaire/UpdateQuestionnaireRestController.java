package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionnaireRestController {

    private final UpdateQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/questionnaires/{questionnaireId}")
    public ResponseEntity<Void> updateQuestionnaire(@PathVariable("kitVersionId") Long kitVersionId,
                                                    @PathVariable("questionnaireId") Long questionnaireId,
                                                    @RequestBody UpdateQuestionnaireRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateQuestionnaire(toParam(kitVersionId, questionnaireId, currentUserId, requestDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateQuestionnaireUseCase.Param toParam(Long kitVersionId,
                                                     Long questionnaireId,
                                                     UUID currentUserId,
                                                     UpdateQuestionnaireRequestDto requestDto) {
        return new UpdateQuestionnaireUseCase.Param(kitVersionId,
            questionnaireId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            currentUserId);
    }
}
