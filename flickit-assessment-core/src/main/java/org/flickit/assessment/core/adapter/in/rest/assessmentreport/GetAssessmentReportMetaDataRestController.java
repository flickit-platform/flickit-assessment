package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetaDataUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetaDataUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentReportMetaDataRestController {

    private final GetAssessmentReportMetaDataUseCase usecase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/report-metadata")
    public ResponseEntity<Result> getAssessmentReportMetaData(@PathVariable UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var reportMetaData = usecase.getAssessmentReportMetaData(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(reportMetaData, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
