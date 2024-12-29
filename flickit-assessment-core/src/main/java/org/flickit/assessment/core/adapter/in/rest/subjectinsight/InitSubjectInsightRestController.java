package org.flickit.assessment.core.adapter.in.rest.subjectinsight;

import lombok.RequiredArgsConstructor;
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

    @PostMapping("/assessments/{assessmentId}/subjects/{subjectId}/init-insights")
    public ResponseEntity<Void> initSubjectInsight(@PathVariable("assessmentId")UUID assessmentId,
                                                   @PathVariable("subjectId") Long subjectId) {
        useCase.initSubjectInsight(toParam(assessmentId, subjectId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private static InitSubjectInsightUseCase.Param toParam(UUID assessmentId, Long subjectId) {
        return new InitSubjectInsightUseCase.Param(assessmentId, subjectId);
    }
}
