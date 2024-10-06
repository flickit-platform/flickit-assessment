package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnairesUseCase;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnairesUseCase.QuestionnaireListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionsRestController {

    private final GetQuestionnairesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/questionnaires")
    public ResponseEntity<PaginatedResponse<QuestionnaireListItem>> getQuestionnaires(@PathVariable("kitVersionId") Long kitVersionId,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = userContext.getUser().id();
        var questionnaires = useCase.getQuestionnaires(toParam(kitVersionId, currentUserId, page, size));
        return new ResponseEntity<>(questionnaires, HttpStatus.OK);
    }

    private static GetQuestionnairesUseCase.Param toParam(Long kitVersionId, UUID currentUserId, int page, int size) {
        return new GetQuestionnairesUseCase.Param(kitVersionId, currentUserId, page, size);
    }
}
