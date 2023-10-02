package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;


import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.DataItems;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CompareAssessmentsRestController {

    private final CompareAssessmentsUseCase useCase;

    @PostMapping("/assessments-compare")
    public ResponseEntity<DataItems> compareAssessments(@RequestBody LinkedHashSet<UUID> assessmentIds) {
        var param = toParam(assessmentIds);
        var result = useCase.compareAssessments(param);
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(LinkedHashSet<UUID> assessmentIds) {
        return new Param(assessmentIds);
    }

    private DataItems toResponseDto(List<CompareAssessmentsUseCase.CompareListItem> items) {
        return new DataItems(items);
    }
}
