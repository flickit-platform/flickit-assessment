package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.report.AssessmentReport;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReportAssessmentRestController {

    private final ReportAssessmentUseCase useCase;

    @GetMapping("/assessments/{assessmentId}/report")
    public ResponseEntity<AssessmentReport> reportAssessment(@PathVariable("assessmentId") UUID assessmentId) {
        var param = toParam(assessmentId);
        var result = useCase.reportAssessment(param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ReportAssessmentUseCase.Param toParam(UUID assessmentId) {
        return new ReportAssessmentUseCase.Param(assessmentId);
    }
}
