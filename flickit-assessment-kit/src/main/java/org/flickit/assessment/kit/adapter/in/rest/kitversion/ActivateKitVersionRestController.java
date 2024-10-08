package org.flickit.assessment.kit.adapter.in.rest.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ActivateKitVersionRestController {

    private final ActivateKitVersionUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/activate")
    public ResponseEntity<Void> activateKitVersion(@PathVariable("kitVersionId") Long kitVersionId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.activateKitVersion(toParam(kitVersionId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UUID currentUserId) {
        return new Param(kitVersionId, currentUserId);
    }
}
