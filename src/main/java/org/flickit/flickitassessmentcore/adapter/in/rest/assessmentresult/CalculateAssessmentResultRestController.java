package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateAssessmentResultUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalculateAssessmentResultRestController {

    private final CalculateAssessmentResultUseCase useCase;

    @PostMapping("/assessment-result/calculate")
    public ResponseEntity<CalculateAssessmentResultResponseDto> calculate(@RequestBody CalculateAssessmentResultRequestDto request) {
        CalculateAssessmentResultUseCase.Param param = toParam(request);

        CalculateAssessmentResultUseCase.Result result = useCase.calculateMaturityLevel(param);
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private CalculateAssessmentResultUseCase.Param toParam(CalculateAssessmentResultRequestDto requestDto) {
        return new CalculateAssessmentResultUseCase.Param(requestDto.assessmentId());
    }

    private CalculateAssessmentResultResponseDto toResponseDto(CalculateAssessmentResultUseCase.Result result) {
        return new CalculateAssessmentResultResponseDto(result.maturityLevel());
    }
}
