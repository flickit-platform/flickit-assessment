package org.flickit.assessment.core.adapter.in.rest.assessmentreport;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentreport.PrepareReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.PrepareReportUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PrepareReportRestController {

    private final PrepareReportUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/prepare-report")
    ResponseEntity<Void> prepareReport(@PathVariable("assessmentId") UUID assessmentId) {
        UUID userId = userContext.getUser().id();
        useCase.create(toParam(assessmentId, userId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
