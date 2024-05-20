package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Param;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireQuestionListRestController {

    private final GetAssessmentQuestionnaireQuestionListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessmentId}/questionnaires/{questionnaireId}")
    public ResponseEntity<PaginatedResponse<Result>> getQuestionnaireQuestionList(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("questionnaireId") Long questionnaireId,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        var param = toParam(assessmentId, questionnaireId, size, page, currentUserId);
        var result = useCase.getQuestionnaireQuestionList(param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long questionnaireId, int size, int page, UUID currentUserId) {
        return new Param(assessmentId, questionnaireId, size, page, currentUserId);
    }
}
