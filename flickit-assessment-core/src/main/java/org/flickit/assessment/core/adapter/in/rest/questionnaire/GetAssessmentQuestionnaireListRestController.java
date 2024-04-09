package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireListRestController {

    private final GetAssessmentQuestionnaireListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessmentId}/questionnaires")
    public ResponseEntity<PaginatedResponse<QuestionnaireListItem>> getAssessmentQuestionnaireList(
        @PathVariable("assessmentId") UUID assessmentId,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getAssessmentQuestionnaireList(toParam(assessmentId, size, page, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAssessmentQuestionnaireListUseCase.Param toParam(UUID assessmentId, int size, int page, UUID currentUserId) {
        return new GetAssessmentQuestionnaireListUseCase.Param(assessmentId, size, page, currentUserId);
    }
}
