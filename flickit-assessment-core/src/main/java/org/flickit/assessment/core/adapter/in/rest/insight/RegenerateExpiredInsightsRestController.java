package org.flickit.assessment.core.adapter.in.rest.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.insight.RegenerateExpiredInsightsUseCase;
import org.flickit.assessment.core.application.port.in.insight.RegenerateExpiredInsightsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RegenerateExpiredInsightsRestController {

    private final RegenerateExpiredInsightsUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/regenerate-expired-insights")
    public ResponseEntity<Void> regenerateExpiredInsights(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.regenerateExpiredInsights(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
