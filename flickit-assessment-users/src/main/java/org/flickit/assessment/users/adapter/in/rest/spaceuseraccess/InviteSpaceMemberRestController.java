package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.InviteSpaceMemberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InviteSpaceMemberRestController {

    private final UserContext userContext;
    private final InviteSpaceMemberUseCase useCase;

    @PostMapping("/spaces/{id}/invite")
    public ResponseEntity<Void> inviteSpaceMember(@PathVariable("id") Long id,
                                                  @RequestBody InviteSpaceMemberRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.inviteMember(toParam(id, request, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    InviteSpaceMemberUseCase.Param toParam(long id, InviteSpaceMemberRequestDto requestDto, UUID currentUserId) {
        return new InviteSpaceMemberUseCase.Param(id, requestDto.email(), currentUserId);
    }
}
