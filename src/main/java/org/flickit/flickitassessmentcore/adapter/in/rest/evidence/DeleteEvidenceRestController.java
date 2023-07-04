package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.DeleteEvidenceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteEvidenceRestController {

    private final DeleteEvidenceUseCase useCase;

    @DeleteMapping("/evidences")
    public ResponseEntity deleteEvidence(@RequestParam("id") UUID evidenceId) {
        useCase.deleteEvidence(new DeleteEvidenceUseCase.Param(evidenceId));
        return new ResponseEntity(null, HttpStatus.OK);
    }
}
