package org.flickit.assessment.core.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.question.GetQuestionIssuesUseCase;
import org.flickit.assessment.core.application.port.in.question.GetQuestionIssuesUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionIssuesRestController {

    private final GetQuestionIssuesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/questions/{questionId}/issues")
    public ResponseEntity<Result> getQuestionIssues(@PathVariable("assessmentId") UUID assessmentId,
                                                    @PathVariable("questionId") Long questionId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getQuestionIssues(toParam(assessmentId, questionId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long questionId, UUID currentUserId) {
        return new Param(questionId, assessmentId, currentUserId);
    }
}
