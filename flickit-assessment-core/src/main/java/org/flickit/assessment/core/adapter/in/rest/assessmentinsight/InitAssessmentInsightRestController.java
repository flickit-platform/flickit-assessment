package org.flickit.assessment.core.adapter.in.rest.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinsight.InitAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinsight.InitAssessmentInsightUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InitAssessmentInsightRestController {

    private final InitAssessmentInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/init-insight")
    public ResponseEntity<Void> initAssessmentInsight(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        useCase.initAssessmentInsight(new Param(assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
