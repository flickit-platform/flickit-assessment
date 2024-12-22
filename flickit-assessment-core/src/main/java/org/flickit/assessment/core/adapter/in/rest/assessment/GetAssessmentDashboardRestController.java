package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentDashboardRestController {

    private final GetAssessmentDashboardUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/dashboard")
    public ResponseEntity<Result> getAssessmentDashboard(@PathVariable UUID assessmentId) {
        var currentUserId = userContext.getUser().id();

        var result = useCase.getAssessmentDashboard(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return Param.builder()
            .id(assessmentId)
            .currentUserId(currentUserId)
            .build();
    }
}
