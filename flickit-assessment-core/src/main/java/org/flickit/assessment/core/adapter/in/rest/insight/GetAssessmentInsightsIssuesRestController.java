package org.flickit.assessment.core.adapter.in.rest.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentInsightsIssuesRestController {

    private final GetAssessmentInsightsIssuesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/insights-issues")
    public ResponseEntity<Result> getInsightsIssues(@PathVariable UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        return new ResponseEntity<>(useCase.getInsightsIssues(toParam(assessmentId, currentUserId)), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
