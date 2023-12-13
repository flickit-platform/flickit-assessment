package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GrantUserAccessToKitController {

    private final GrantUserAccessToKitUseCase useCase;

    @PostMapping("assessment-kits/{kitId}/users/{currentUserEmail}")
    public ResponseEntity<Void> grantUserAccessToKit(@PathVariable("kitId") Long kitId,
                                                     @PathVariable("currentUserEmail") String currentUserEmail,
                                                     @RequestBody GrantUserAccessToKitRequestDto request) {

//        TODO get currentUserEmail from jwt token
        useCase.grantUserAccessToKit(toParam(kitId, request, currentUserEmail));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private GrantUserAccessToKitUseCase.Param toParam(Long kitId, GrantUserAccessToKitRequestDto request, String currentUserEmail) {
        return new GrantUserAccessToKitUseCase.Param(kitId, request.userEmail(), currentUserEmail);
    }
}
