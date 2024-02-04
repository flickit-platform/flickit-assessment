package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InviteExpertGroupMemberRestController {

    private final InviteExpertGroupMemberUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups/{id}/invite")
    public ResponseEntity<CreateExpertGroupResponseDto> createExpertGroup(@PathVariable("id") long expertGroupId,
                                                                          @JsonProperty ("userId") UUID userId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.addMember(toParam(expertGroupId, userId, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private InviteExpertGroupMemberUseCase.Param toParam(long expertGroupId, UUID userId, UUID currentUserId) {
        return new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
    }
}
