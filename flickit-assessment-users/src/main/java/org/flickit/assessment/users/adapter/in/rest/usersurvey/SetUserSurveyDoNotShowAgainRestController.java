package org.flickit.assessment.users.adapter.in.rest.usersurvey;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase;
import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SetUserSurveyDoNotShowAgainRestController {

    private final SetUserSurveyDoNotShowAgainUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/user-surveys/dont-show-again")
    public ResponseEntity<Void> setDoNotShowAgain(@RequestBody SetUserSurveyDoNotShowAgainRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.setDontShowAgain(toParam(requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(SetUserSurveyDoNotShowAgainRequestDto requestDto, UUID currentUserId) {
        return new Param(requestDto.assessmentId(), currentUserId);
    }
}
