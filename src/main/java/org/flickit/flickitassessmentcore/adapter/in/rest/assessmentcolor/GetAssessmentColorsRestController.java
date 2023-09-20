package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentcolor;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class GetAssessmentColorsRestController {

    private final GetAssessmentColorsUseCase useCase;

    @GetMapping("/assessment-colors")
    public ResponseEntity<ResponseDto> getAssessmentColors() {
        var response = useCase.getAssessmentColors();
        ResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private ResponseDto toResponseDto(Result result) {
        return new ResponseDto(result.defaultColor(), result.colors());
    }
}
