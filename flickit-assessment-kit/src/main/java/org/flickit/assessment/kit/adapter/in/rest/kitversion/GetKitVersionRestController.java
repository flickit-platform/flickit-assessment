package org.flickit.assessment.kit.adapter.in.rest.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitVersionRestController {

    private final GetKitVersionUseCase getKitVersionUseCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}")
    public ResponseEntity<Result> getKitVersionId(@PathVariable("kitVersionId") Long kitVersionId) {
        var currentUserId = userContext.getUser().id();
        var param = toParam(kitVersionId, currentUserId);

        return new ResponseEntity<>(getKitVersionUseCase.getKitVersion(param), HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UUID currentUserId) {
        return new Param(kitVersionId, currentUserId);
    }
}
