package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddExpertGroupMemberRestController {

    private final AddExpertGroupMemberUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/expert-groups")
    public ResponseEntity<CreateExpertGroupResponseDto> createExpertGroup(@RequestParam UUID user) {
        UUID currentUserId = userContext.getUser().id();
        useCase.addMember(user, currentUserId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
