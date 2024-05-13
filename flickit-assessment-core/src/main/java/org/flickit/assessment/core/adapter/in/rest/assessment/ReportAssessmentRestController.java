package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
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
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/report")
    public ResponseEntity<ReportAssessmentUseCase.Result> reportAssessment(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var param = toParam(assessmentId, currentUserId);
        var response = useCase.reportAssessment(param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ReportAssessmentUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new ReportAssessmentUseCase.Param(assessmentId, currentUserId);
    }
}
