package org.flickit.assessment.users.adapter.in.rest.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceinvitee.DeleteSpaceInvitationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteSpaceInvitationRestController {

    private final DeleteSpaceInvitationUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/spaces/{spaceId}/invitation")
    public ResponseEntity<Void> deleteSpaceInvitation(@PathVariable("spaceId") long spaceId,
                                                      @RequestBody DeleteSpaceInvitationRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteInvitation(toParam(spaceId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteSpaceInvitationUseCase.Param toParam(long spaceId, DeleteSpaceInvitationRequestDto requestDto, UUID currentUserId) {
        return new DeleteSpaceInvitationUseCase.Param(spaceId, requestDto.email(), currentUserId);
    }
}
