package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CalculateAssessmentRestController {

    private final CalculateAssessmentUseCase useCase;

    @PostMapping("/assessments/{assessmentId}/calculate")
    public ResponseEntity<CalculateAssessmentResponseDto> calculate(@PathVariable("assessmentId") UUID assessmentId) {
        var param = toParam(assessmentId);
        var result = useCase.calculateMaturityLevel(param);
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private CalculateAssessmentUseCase.Param toParam(UUID assessmentId) {
        return new CalculateAssessmentUseCase.Param(assessmentId);
    }

    private CalculateAssessmentResponseDto toResponseDto(CalculateAssessmentUseCase.Result result) {
        return new CalculateAssessmentResponseDto(result.maturityLevel());
    }
}
