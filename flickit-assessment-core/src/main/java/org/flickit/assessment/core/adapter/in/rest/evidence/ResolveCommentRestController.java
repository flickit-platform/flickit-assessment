package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.ResolveCommentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ResolveCommentRestController {

    private final ResolveCommentUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/evidences/{evidenceId}/resolve")
    public ResponseEntity<Void> resolveComment(@PathVariable("evidenceId") UUID evidenceId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.resolveComment(toParam(evidenceId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResolveCommentUseCase.Param toParam(UUID evidenceId, UUID currentUserId) {
        return new ResolveCommentUseCase.Param(evidenceId, currentUserId);
    }
}
