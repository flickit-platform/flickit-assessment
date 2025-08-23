package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.LeaveExpertGroupUseCase;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.LeaveExpertGroupUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LeaveExpertGroupRestController {

    private final LeaveExpertGroupUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/expert-groups/{id}/leave")
    public ResponseEntity<Void> leaveExpertGroup(@PathVariable Long id) {
        UUID currentUserId = userContext.getUser().id();
        useCase.leaveExpertGroup(toParam(id, currentUserId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(Long id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }
}
