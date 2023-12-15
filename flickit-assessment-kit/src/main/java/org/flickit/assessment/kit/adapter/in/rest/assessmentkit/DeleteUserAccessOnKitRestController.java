package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteUserAccessOnKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteUserAccessOnKitRestController {

    private final DeleteUserAccessOnKitUseCase useCase;

    @DeleteMapping("assessment-kits/{kitId}/users")
    public ResponseEntity<Void> deleteUserAccess(@PathVariable("kitId") Long kitId, @RequestParam("userId") UUID userId) {
        useCase.delete(toParam(kitId, userId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DeleteUserAccessOnKitUseCase.Param toParam(Long kitId, UUID userId) {
        return new DeleteUserAccessOnKitUseCase.Param(kitId, userId);
    }
}
