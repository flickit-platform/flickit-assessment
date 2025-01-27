package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.PublishAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.PublishAssessmentReportUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PublishAssessmentReportRestController {

    private final PublishAssessmentReportUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/publish-report")
    public ResponseEntity<Void> publishAssessmentReport(@PathVariable UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        useCase.publishAssessmentReport(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
