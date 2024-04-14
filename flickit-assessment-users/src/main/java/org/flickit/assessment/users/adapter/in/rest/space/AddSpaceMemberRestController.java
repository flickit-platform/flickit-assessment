package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.flickit.assessment.users.application.port.in.spaceaccess.AddSpaceMemberUseCase;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddSpaceMemberRestController {

    private final UserContext userContext;
    private final AddSpaceMemberUseCase addSpaceMemberUseCase;

    @PostMapping("/spaces/{id}/member")
    public ResponseEntity<Void> addSpaceMember(@PathVariable("id") Long id,
                                               @RequestParam("email") String email){
        UUID currentUserId = userContext.getUser().id();

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
