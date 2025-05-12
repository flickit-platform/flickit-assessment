package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentPublicReportRestController {

    private final GetAssessmentPublicReportUseCase useCase;
    private final UserContext userContext;

    @GetMapping("public/assessments/graphical-report/{linkHash}")
    public ResponseEntity<Result> getAssessmentPublicReport(@PathVariable UUID linkHash) {
        var currentUserId = userContext.isAuthenticated() ? userContext.getUser().id() : null;
        var assessmentReport = useCase.getAssessmentPublicReport(toParam(linkHash, currentUserId));
        return new ResponseEntity<>(assessmentReport, HttpStatus.OK);
    }

    private Param toParam(UUID linkHash, UUID currentUserId) {
        return new Param(linkHash, currentUserId);
    }
}
