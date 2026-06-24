package org.flickit.assessment.users.adapter.in.rest.usersurvey;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.usersurvey.InitUserSurveyUseCase;
import org.flickit.assessment.users.application.port.in.usersurvey.InitUserSurveyUseCase.Param;
import org.flickit.assessment.users.application.port.in.usersurvey.InitUserSurveyUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InitUserSurveyRestController {

    private final InitUserSurveyUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/init-survey")
    public ResponseEntity<Result> initUserSurvey(@RequestBody InitUserSurveyRequestDto requestDto) {
        UUID currentUserid = userContext.getUser().id();

        return new ResponseEntity<>(useCase.initUserSurvey(toParam(requestDto, currentUserid)), HttpStatus.CREATED);
    }

    private Param toParam(InitUserSurveyRequestDto requestDto, UUID currentUserid) {
        return new Param(requestDto.assessmentId(),
            currentUserid);
    }
}
