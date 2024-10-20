package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteExpertGroupMembersRestController {

    private final DeleteExpertGroupMemberUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/expert-groups/{id}/members/{userId}")
    public ResponseEntity<Void> deleteExpertGroupMembers(
        @PathVariable("id") long id,
        @PathVariable("userId") UUID userId) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteMember(toParam(id, userId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteExpertGroupMemberUseCase.Param toParam(long expertGroupId, UUID userId, UUID currentUserId) {
        return new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
    }
}
