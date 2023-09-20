package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentcolor;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class GetAssessmentColorsRestController {

    private final GetAssessmentColorsUseCase useCase;

    @GetMapping("/assessment-colors")
    public ResponseEntity<GetAssessmentColorsUseCase.AssessmentColors> createAssessment() {
        var response = useCase.getAssessmentColors();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
