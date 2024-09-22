package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionnaireRestController {

    private final UpdateQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessment-kits/{kitId}/questionnaires/{questionnaireId}")
    public ResponseEntity<Void> updateQuestionnaire(@PathVariable("kitId") Long kitId,
                                                    @PathVariable("questionnaireId") Long questionnaireId,
                                                    @RequestParam UpdateQuestionnaireRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateQuestionnaire(toParam(kitId, questionnaireId, currentUserId, requestDto));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private UpdateQuestionnaireUseCase.Param toParam(Long kitId,
                                                     Long questionnaireId,
                                                     UUID currentUserId,
                                                     UpdateQuestionnaireRequestDto requestDto) {
        return new UpdateQuestionnaireUseCase.Param(kitId,
            questionnaireId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            currentUserId);
    }
}
