package org.flickit.assessment.core.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.question.GetAssessmentQuestionUseCase;
import org.flickit.assessment.core.application.port.in.question.GetAssessmentQuestionUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.flickit.assessment.core.application.port.in.question.GetAssessmentQuestionUseCase.*;

@RestController
@RequiredArgsConstructor
public class GetAssessmentQuestionRestController {

    private final GetAssessmentQuestionUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/questions/{questionId}")
    public ResponseEntity<Result> getAssessmentQuestion(@PathVariable UUID assessmentId,
                                                        @PathVariable Long questionId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getQuestion(toParam(assessmentId, questionId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long questionId, UUID currentUserId) {
        return new Param(assessmentId, questionId, currentUserId);
    }
}
