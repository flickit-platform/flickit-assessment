package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase.Param;

@RestController
@RequiredArgsConstructor
public class GetAssessmentNextQuestionnaireRestController {

    private final GetAssessmentNextQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/questionnaires/{questionnaireId}/next")
    public ResponseEntity<GetAssessmentNextQuestionnaireResponseDto> getAssessmentNextQuestionnaire(@PathVariable("assessmentId") UUID assessmentId,
                                                                 @PathVariable("questionnaireId") Long questionnaireId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getNextQuestionnaire(toParam(assessmentId, questionnaireId, currentUserId));
        var response = toResponse(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long questionnaireId, UUID currentUserId) {
        return new Param(assessmentId, questionnaireId, currentUserId);
    }

    private GetAssessmentNextQuestionnaireResponseDto toResponse(GetAssessmentNextQuestionnaireUseCase.Result result) {
        return GetAssessmentNextQuestionnaireResponseDto.of(result);
    }
}
