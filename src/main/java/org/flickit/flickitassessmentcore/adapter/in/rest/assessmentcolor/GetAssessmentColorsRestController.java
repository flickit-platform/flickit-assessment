package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentcolor;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.DataItems;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetAssessmentColorsRestController {

    private final GetAssessmentColorsUseCase useCase;

    @GetMapping("/assessment-colors")
    public ResponseEntity<DataItems> createAssessment() {
        var response = useCase.getAssessmentColors();
        List<ColorDto> responseDto = response.stream()
            .map(x -> new ColorDto(x.getId(), x.getTitle(), x.getCode()))
            .toList();
        return new ResponseEntity<>(new DataItems(responseDto), HttpStatus.OK);
    }
}
