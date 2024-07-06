package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetEvidenceRestController {

    private final GetEvidenceUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/evidences/{id}")
    public ResponseEntity<Result> getEvidence(@PathVariable("id") UUID id) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getEvidence(toParam(id, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }
}
