package org.flickit.assessment.core.adapter.in.rest.assessmentcolor;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class GetAssessmentColorsRestController {

    private final GetAssessmentColorsUseCase useCase;

    @GetMapping("/assessment-colors")
    public ResponseEntity<GetAssessmentColorsResponseDto> getAssessmentColors() {
        var response = useCase.getAssessmentColors();
        GetAssessmentColorsResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetAssessmentColorsResponseDto toResponseDto(Result result) {
        return new GetAssessmentColorsResponseDto(result.defaultColor(), result.colors());
    }
}
