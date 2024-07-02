package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateExpertGroupRestController {

    private final UpdateExpertGroupUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}")
    public ResponseEntity<Void> updateExpertGroup(@PathVariable("id") Long id,
                                                  @RequestBody UpdateExpertGroupRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateExpertGroup(toParam(id, request, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long id, UpdateExpertGroupRequestDto request, UUID currentUserId) {
        return new Param(
            id,
            request.title(),
            request.bio(),
            request.about(),
            request.website(),
            currentUserId);
    }
}
