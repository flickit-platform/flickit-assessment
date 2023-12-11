package org.flickit.assessment.kit.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.user.DeleteUserAccessUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeleteUserAccessRestController {

    private final DeleteUserAccessUseCase useCase;

    @DeleteMapping("assessment-kits/{kitId}/users")
    public ResponseEntity<Void> deleteUserAccess(@PathVariable("kitId") Long kitId, @RequestParam("userId") Long userId) {
        useCase.delete(toParam(kitId, userId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DeleteUserAccessUseCase.Param toParam(Long kitId, Long userId) {
        return new DeleteUserAccessUseCase.Param(kitId, userId);
    }
}
