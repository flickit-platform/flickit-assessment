package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.DeleteSpaceMemberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteSpaceMemberRestController {

    private final DeleteSpaceMemberUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/spaces/{spaceId}/members/{userId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("spaceId") long spaceId,
                                             @PathVariable("userId") UUID userId) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteMember(toParam(spaceId, userId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteSpaceMemberUseCase.Param toParam(long spaceId, UUID userId, UUID currentUserId) {
        return new DeleteSpaceMemberUseCase.Param(spaceId, userId, currentUserId);
    }
}
