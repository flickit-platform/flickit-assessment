package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
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

    @PostMapping("assessment-kits/{kitId}/users/{ownerId}")
    public ResponseEntity<Void> grantUserAccessToKit(@PathVariable("kitId") Long kitId,
                                                     @PathVariable("ownerId") UUID ownerId,
                                                     @RequestBody GrantUserAccessToKitRequestDto request) {

//        TODO get ownerId from jwt token
        useCase.grantUserAccessToKit(toParam(kitId, request, ownerId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private GrantUserAccessToKitUseCase.Param toParam(Long kitId, GrantUserAccessToKitRequestDto request, UUID ownerId) {
        return new GrantUserAccessToKitUseCase.Param(kitId, request.userEmail(), ownerId);
    }
}
