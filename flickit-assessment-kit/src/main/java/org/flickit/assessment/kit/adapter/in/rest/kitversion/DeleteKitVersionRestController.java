package org.flickit.assessment.kit.adapter.in.rest.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitversion.DeleteKitVersionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteKitVersionRestController {

    private final DeleteKitVersionUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}")
    public ResponseEntity<Void> deleteKitVersion(@PathVariable("kitVersionId") Long kitVersionId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteKitVersion(toParam(kitVersionId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteKitVersionUseCase.Param toParam(Long kitVersionId, UUID currentUserId) {
        return new DeleteKitVersionUseCase.Param(kitVersionId, currentUserId);
    }
}
