package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase;
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

    @DeleteMapping("/evidences/{id}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable("id") UUID id) {
        useCase.deleteEvidence(new DeleteEvidenceUseCase.Param(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
