package org.flickit.assessment.core.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.maturitylevel.GetAssessmentMaturityLevelsUseCase;
import org.flickit.assessment.core.application.port.in.maturitylevel.GetAssessmentMaturityLevelsUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentMaturityLevelsRestController {

    private final GetAssessmentMaturityLevelsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/maturity-levels")
    public ResponseEntity<Result> submitAnswer(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getAssessmentMaturityLevels(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}


