package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentAttributesRestController {

    private final GetAssessmentAttributesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/attributes")
    public ResponseEntity<Result> getAssessmentAttributes(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getAssessmentAttributes(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
