package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GenerateAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GenerateAllAssessmentInsightsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GenerateAllAssessmentInsightsRestController {

    private final GenerateAllAssessmentInsightsUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/generate-insights")
    public ResponseEntity<Void> generateAllAssessmentInsights(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.generateAllAssessmentInsights(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
