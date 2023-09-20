package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase.AssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CheckComparativeAssessmentsRestController {

    private final CheckComparativeAssessmentsUseCase useCase;

    @PutMapping("/compare/assessments")
    public ResponseEntity<CheckComparativeAssessmentsResponseDto> checkComparativeAssessments(@RequestParam("assessmentIds") List<UUID> assessmentIds) {
        var result = useCase.checkComparativeAssessments(toParam(assessmentIds));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private CheckComparativeAssessmentsUseCase.Param toParam(List<UUID> assessmentIds) {
        return new CheckComparativeAssessmentsUseCase.Param(assessmentIds);
    }

    private CheckComparativeAssessmentsResponseDto toResponse(List<AssessmentListItem> assessmentListItems) {
        return new CheckComparativeAssessmentsResponseDto(assessmentListItems);
    }
}
