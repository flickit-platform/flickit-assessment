package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.domain.QuestionnaireListItem;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnaireListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionnaireListRestController {

    private final GetQuestionnaireListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessmentId}/questionnaires")
    public ResponseEntity<PaginatedResponse<QuestionnaireListItem>> getQuestionnaireList(
        @PathVariable("assessmentId") UUID assessmentId,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page) {

        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getQuestionnaireList(toParam(assessmentId, currentUserId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetQuestionnaireListUseCase.Param toParam(UUID assessmentId, UUID currentUserId, int size, int page) {
        return new GetQuestionnaireListUseCase.Param(assessmentId, currentUserId, size, page);
    }
}
