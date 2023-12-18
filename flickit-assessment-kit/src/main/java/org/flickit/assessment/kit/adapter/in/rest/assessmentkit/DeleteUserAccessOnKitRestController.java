package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteUserAccessOnKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteUserAccessOnKitRestController {

    private final DeleteUserAccessOnKitUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("assessment-kits/{kitId}/users")
    public ResponseEntity<Void> deleteUserAccess(@PathVariable("kitId") Long kitId,
                                                 @RequestBody DeleteUserAccessOnKitRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        useCase.delete(toParam(kitId, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DeleteUserAccessOnKitUseCase.Param toParam(Long kitId, DeleteUserAccessOnKitRequestDto request, UUID currentUserId) {
        return new DeleteUserAccessOnKitUseCase.Param(kitId, request.email(), currentUserId);
    }
}
