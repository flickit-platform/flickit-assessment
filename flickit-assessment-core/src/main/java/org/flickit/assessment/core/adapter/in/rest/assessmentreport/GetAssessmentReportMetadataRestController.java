package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentReportMetadataRestController {

    private final GetAssessmentReportMetadataUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/report-metadata")
    public ResponseEntity<Result> getAssessmentReportMetadata(@PathVariable UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var reportMetadata = useCase.getAssessmentReportMetadata(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(reportMetadata, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
