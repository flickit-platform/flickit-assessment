package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteExpertGroupRestController {

    private final UserContext userContext;
    private final DeleteExpertGroupUseCase useCase;

    @DeleteMapping("/expert-groups/{id}")
    public ResponseEntity<Void> deleteExpertGroup(@PathVariable("id") Long id) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteExpertGroup(toParam(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(long id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }
}
