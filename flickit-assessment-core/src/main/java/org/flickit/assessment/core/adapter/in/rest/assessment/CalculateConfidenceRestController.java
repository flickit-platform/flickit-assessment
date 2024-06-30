package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CalculateConfidenceRestController {

    private final CalculateConfidenceUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/calculate-confidence")
    public ResponseEntity<CalculateConfidenceResponseDto> calculate(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        var response = useCase.calculate(toParam(assessmentId, currentUserId));
        var responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }

    private CalculateConfidenceResponseDto toResponseDto(Result response) {
        return new CalculateConfidenceResponseDto(response);
    }

}
