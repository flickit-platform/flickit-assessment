package org.flickit.assessment.kit.adapter.in.rest.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ValidateKitVersionRestController {

    private final ValidateKitVersionUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/validate")
    public ResponseEntity<Result> validateKitVersion(@PathVariable("kitVersionId") Long kitVersionId) {
        var currentUserId = userContext.getUser().id();
        return new ResponseEntity<>(useCase.validate(toParam(kitVersionId, currentUserId)), HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UUID currentUserId) {
        return new Param(kitVersionId, currentUserId);
    }
}
