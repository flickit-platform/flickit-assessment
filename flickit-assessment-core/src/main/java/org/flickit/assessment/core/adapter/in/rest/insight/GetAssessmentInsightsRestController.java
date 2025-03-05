package org.flickit.assessment.core.adapter.in.rest.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.insight.GetAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.in.insight.GetAssessmentInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.in.insight.GetAssessmentInsightsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentInsightsRestController {

    private final GetAssessmentInsightsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessmentId}/insights")
    ResponseEntity<Result> getAssessmentInsights(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getAssessmentInsights(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
