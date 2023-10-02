package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountAssessmentsUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountAssessmentsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CountAssessmentsRestController {

    private final CountAssessmentsUseCase useCase;

    @GetMapping("/assessments/counters")
    public ResponseEntity<CountAssessmentsResponseDto> countAssessments(
        @RequestParam("assessmentKitId") Long assessmentKitId,
        @RequestParam(value = "deleted", defaultValue = "false") Boolean deleted,
        @RequestParam(value = "notDeleted", defaultValue = "false") Boolean notDeleted,
        @RequestParam(value = "total", defaultValue = "false") Boolean total
    ) {
        var result = useCase.countAssessments(toParam(assessmentKitId, deleted, notDeleted, total));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(Long assessmentKitId, Boolean includeDeleted, Boolean includeNotDeleted, Boolean total) {
        return new Param(assessmentKitId, includeDeleted, includeNotDeleted, total);
    }

    private CountAssessmentsResponseDto toResponseDto(Result result) {
        return new CountAssessmentsResponseDto(result.totalCount(), result.deletedCount(), result.notDeletedCount());
    }

}
