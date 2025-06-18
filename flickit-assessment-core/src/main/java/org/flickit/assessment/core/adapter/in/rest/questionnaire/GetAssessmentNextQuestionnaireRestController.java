package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase.*;

@RestController
@RequiredArgsConstructor
public class GetAssessmentNextQuestionnaireRestController {

    private final GetAssessmentNextQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/next-questionnaire")
    public ResponseEntity<Result> getAssessmentNextQuestionnaire(@PathVariable("assessmentId") UUID assessmentId,
                                                                 @RequestBody GetAssessmentNextQuestionnaireRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getNextQuestionnaire(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, GetAssessmentNextQuestionnaireRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.questionnaireId(), currentUserId);
    }
}
