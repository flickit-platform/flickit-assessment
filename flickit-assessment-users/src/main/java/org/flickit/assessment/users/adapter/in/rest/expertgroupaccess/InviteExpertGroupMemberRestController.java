package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InviteExpertGroupMemberRestController {

    private final InviteExpertGroupMemberUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups/{id}/invite")
    public ResponseEntity<Void> inviteExpertGroupMember(
        @PathVariable("id") Long expertGroupId,
        @RequestBody InviteExpertGroupMemberRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.inviteMember(toParam(expertGroupId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private InviteExpertGroupMemberUseCase.Param toParam(long expertGroupId,
                                                         InviteExpertGroupMemberRequestDto requestDto,
                                                         UUID currentUserId) {
        return new InviteExpertGroupMemberUseCase.Param(expertGroupId, requestDto.userId(), currentUserId);
    }
}
