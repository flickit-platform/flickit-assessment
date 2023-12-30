package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteKitUserAccessUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteKitUserAccessRestController {

    private final DeleteKitUserAccessUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("assessment-kits/{kitId}/users/{userId}")
    public ResponseEntity<Void> deleteKitUserAccess(@PathVariable("kitId") Long kitId,
                                                 @PathVariable("userId") UUID userId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.delete(toParam(kitId, userId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DeleteKitUserAccessUseCase.Param toParam(Long kitId, UUID userId, UUID currentUserId) {
        return new DeleteKitUserAccessUseCase.Param(kitId, userId, currentUserId);
    }
}
