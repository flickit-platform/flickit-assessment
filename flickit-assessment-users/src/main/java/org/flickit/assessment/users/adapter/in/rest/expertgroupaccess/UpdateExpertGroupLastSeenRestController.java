package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.UpdateExpertGroupLastSeenUseCase;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.UpdateExpertGroupLastSeenUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateExpertGroupLastSeenRestController {

    private final UpdateExpertGroupLastSeenUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}/seen")
    public ResponseEntity<Void> updateExpertGroupLastSeen(@PathVariable("id") Long expertGroupId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateLastSeen(toParam(expertGroupId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long expertGroupId, UUID currentUserId) {
        return new Param(expertGroupId, currentUserId);
    }
}
