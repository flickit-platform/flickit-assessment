package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.confidencelevel.CalculateConfidenceLevelUseCase;
import org.flickit.assessment.core.application.port.in.confidencelevel.CalculateConfidenceLevelUseCase.ConfidenceLevelResult;
import org.flickit.assessment.core.application.port.in.confidencelevel.CalculateConfidenceLevelUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CalculateConfidenceLevelRestController {

    private final CalculateConfidenceLevelUseCase useCase;

    @GetMapping("/assessments/{assessmentId}/calculate-confidence-level")
    public ResponseEntity<CalculateConfidenceLevelResponseDto> calculate(@PathVariable("assessmentId") UUID assessmentId) {
        var response = useCase.calculate(toParam(assessmentId));
        var responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId) {
        return new Param(assessmentId);
    }

    private CalculateConfidenceLevelResponseDto toResponseDto(ConfidenceLevelResult response) {
        return new CalculateConfidenceLevelResponseDto(response);
    }

}
