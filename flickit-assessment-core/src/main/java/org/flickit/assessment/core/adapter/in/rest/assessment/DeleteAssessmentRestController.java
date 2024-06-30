package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.DeleteAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAssessmentRestController {

    private final DeleteAssessmentUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/assessments/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable("id") UUID id) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAssessment(new DeleteAssessmentUseCase.Param(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
