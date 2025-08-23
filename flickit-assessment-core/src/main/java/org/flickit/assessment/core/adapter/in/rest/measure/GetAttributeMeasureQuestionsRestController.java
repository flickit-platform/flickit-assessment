package org.flickit.assessment.core.adapter.in.rest.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase.Param;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributeMeasureQuestionsRestController {

    private final GetAttributeMeasureQuestionsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/attributes/{attributeId}/measures/{measureId}")
    public ResponseEntity<Result> getAttributeMeasureQuestions(@PathVariable("assessmentId") UUID assessmentId,
                                                               @PathVariable("attributeId") Long attributeId,
                                                               @PathVariable("measureId") Long measureId) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getAttributeMeasureQuestions(toParam(assessmentId, attributeId, measureId, currentUserId));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long attributeId, Long measureId, UUID currentUserId) {
        return new Param(assessmentId, attributeId, measureId, currentUserId);
    }
}
