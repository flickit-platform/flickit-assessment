package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateQuickAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateQuickAssessmentReportUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateQuickAssessmentReportRestController {

    private final CreateQuickAssessmentReportUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/quick-report")
    ResponseEntity<Void> createQuickAssessmentReport(@PathVariable("assessmentId") UUID assessmentId) {
        UUID userId = userContext.getUser().id();
        useCase.create(toParam(assessmentId, userId));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);

    }
}
