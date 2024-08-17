package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.GetUserSubscriberHashUseCase;
import org.flickit.assessment.users.application.port.in.user.GetUserSubscriberHashUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetUserSubscriberHashRestController {

    private final GetUserSubscriberHashUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/users/subscriber-hash")
    public ResponseEntity<GetUserSubscriberHashResponseDto> getUserSubscriberHash() {
        UUID currentUserId = userContext.getUser().id();
        var responseDto = toResponseDto(useCase.getUserSubscriberHash(new Param(currentUserId)));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetUserSubscriberHashResponseDto toResponseDto(GetUserSubscriberHashUseCase.Result result) {
        return new GetUserSubscriberHashResponseDto(result.subscriberHash());
    }
}
