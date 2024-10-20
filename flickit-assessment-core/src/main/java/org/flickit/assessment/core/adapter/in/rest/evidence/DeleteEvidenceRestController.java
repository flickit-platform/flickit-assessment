package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteEvidenceRestController {

    private final DeleteEvidenceUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/evidences/{id}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable("id") UUID id) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteEvidence(new Param(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
