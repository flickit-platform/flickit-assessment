package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.AddExpertGroupMemberUseCase;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddExpertGroupMemberRestController {

    private final AddExpertGroupMemberUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups/{id}/members/{userId}")
    public ResponseEntity<CreateExpertGroupResponseDto> createExpertGroup(@PathVariable("id") long expertGroupId,
                                                                          @PathVariable("userId") UUID userId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.addMember(toParam(expertGroupId, userId, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private AddExpertGroupMemberUseCase.Param toParam(long expertGroupId, UUID userId, UUID currentUserId) {
        return new AddExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
    }
}
