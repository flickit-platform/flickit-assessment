package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ResolveAssessmentCommentsRestController {

    private final ResolveAssessmentCommentsUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/resolve-comments")
    public ResponseEntity<Void> resolveAssessmentComments(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();

        useCase.resolveAllComments(toParam(assessmentId, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
