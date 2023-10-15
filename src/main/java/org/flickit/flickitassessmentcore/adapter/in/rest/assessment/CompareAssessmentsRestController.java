package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;


import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.DataItems;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase.CompareListItem;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CompareAssessmentsRestController {

    private final CompareAssessmentsUseCase useCase;

    @GetMapping("/assessments/compare")
    public ResponseEntity<DataItems> compareAssessments(@RequestParam List<UUID> assessmentIds) {
        var result = useCase.compareAssessments(toParam(assessmentIds));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(List<UUID> assessmentIds) {
        return new Param(assessmentIds);
    }

    private DataItems toResponseDto(List<CompareListItem> items) {
        return new DataItems(items);
    }
}
