package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GrantUserAccessToKitController {

    private final GrantUserAccessToKitUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessment-kits/{kitId}/users")
    public ResponseEntity<Void> grantUserAccessToKit(@PathVariable("kitId") Long kitId,
                                                     @RequestBody GrantUserAccessToKitRequestDto request) {
        UUID currentUserId = userContext.getUser().id();
        useCase.grantUserAccessToKit(toParam(kitId, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private GrantUserAccessToKitUseCase.Param toParam(Long kitId, GrantUserAccessToKitRequestDto request, UUID currentUserId) {
        return new GrantUserAccessToKitUseCase.Param(kitId, request.userId(), currentUserId);
    }
}
