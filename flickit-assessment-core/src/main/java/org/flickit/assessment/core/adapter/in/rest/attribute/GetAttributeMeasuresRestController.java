package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeMeasuresUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeMeasuresUseCase.Param;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeMeasuresUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributeMeasuresRestController {

    private final GetAttributeMeasuresUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/attributes/{attributeId}/measures")
    public ResponseEntity<Result> getAttributeMeasures(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "order", required = false) String order) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getAttributeMeasures(toParam(assessmentId, attributeId, sort, order, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long attributeId, String sort, String order, UUID currentUserId) {
        return new Param(assessmentId,
            attributeId,
            sort,
            order,
            currentUserId);
    }
}
