package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionnaireQuestionsUseCase;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionnaireQuestionsUseCase.QuestionListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionnaireQuestionsRestController {

    private final UserContext userContext;
    private final GetQuestionnaireQuestionsUseCase useCase;

    @GetMapping("/kit-versions/{kitVersionId}/questionnaires/{questionnaireId}/questions")
    public ResponseEntity<PaginatedResponse<QuestionListItem>> getQuestionnaireQuestions(
        @PathVariable("kitVersionId") Long kitVersionId,
        @PathVariable("questionnaireId") Long questionnaireId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getQuestionnaireQuestions(toParam(kitVersionId, questionnaireId, currentUserId, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetQuestionnaireQuestionsUseCase.Param toParam(Long kitVersionId,
                                                           Long questionnaireId,
                                                           UUID currentUserId,
                                                           int page,
                                                           int size) {
        return new GetQuestionnaireQuestionsUseCase.Param(kitVersionId, questionnaireId, currentUserId, page, size);
    }
}
