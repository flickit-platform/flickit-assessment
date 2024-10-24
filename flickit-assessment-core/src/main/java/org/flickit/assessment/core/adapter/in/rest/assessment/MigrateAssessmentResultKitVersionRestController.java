package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MigrateAssessmentResultKitVersionRestController {

    private final MigrateAssessmentResultKitVersionUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/migrate-kit-version")
    public ResponseEntity<Void> migrateAssessmentResultKitVersion(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        useCase.migrateKitVersion(toParam(assessmentId, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
