package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.CheckCreateSpaceUseCase;
import org.flickit.assessment.users.application.port.in.space.CheckCreateSpaceUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckCreateSpaceRestController {

    private final CheckCreateSpaceUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/check-create-space")
    public ResponseEntity<Result> checkCreateSpace() {
        var currentUserId = userContext.getUser().id();
        var result = useCase.checkCreateSpace(new CheckCreateSpaceUseCase.Param(currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
