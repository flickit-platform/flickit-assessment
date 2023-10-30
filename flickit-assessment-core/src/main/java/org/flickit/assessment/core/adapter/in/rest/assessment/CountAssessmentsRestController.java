package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.CountAssessmentsUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CountAssessmentsUseCase.Param;
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
        @RequestParam(value = "assessmentKitId", required = false) Long assessmentKitId,
        @RequestParam(value = "spaceId", required = false) Long spaceId,
        @RequestParam(value = "deleted", required = false) boolean deleted,
        @RequestParam(value = "notDeleted", required = false) boolean notDeleted,
        @RequestParam(value = "total", required = false) boolean total
    ) {
        var result = useCase.countAssessments(toParam(assessmentKitId, spaceId, deleted, notDeleted, total));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(Long assessmentKitId, Long spaceId, boolean deleted, boolean notDeleted, boolean total) {
        return new Param(assessmentKitId, spaceId, deleted, notDeleted, total);
    }

    private CountAssessmentsResponseDto toResponseDto(CountAssessmentsUseCase.Result result) {
        return new CountAssessmentsResponseDto(result.totalCount(), result.deletedCount(), result.notDeletedCount());
    }

}
