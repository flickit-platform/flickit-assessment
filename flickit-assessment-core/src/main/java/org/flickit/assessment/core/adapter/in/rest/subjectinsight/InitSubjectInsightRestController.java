package org.flickit.assessment.core.adapter.in.rest.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.subjectinsight.InitSubjectInsightUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InitSubjectInsightRestController {

    private final InitSubjectInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/subjects/{subjectId}/init-insight")
    public ResponseEntity<Void> initSubjectInsight(@PathVariable("assessmentId")UUID assessmentId,
                                                   @PathVariable("subjectId") Long subjectId) {
        var currentUserId = userContext.getUser().id();
        useCase.initSubjectInsight(toParam(assessmentId, subjectId, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private static InitSubjectInsightUseCase.Param toParam(UUID assessmentId, Long subjectId, UUID currentUserId) {
        return new InitSubjectInsightUseCase.Param(assessmentId, subjectId, currentUserId);
    }
}
