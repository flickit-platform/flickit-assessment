package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetComparableAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetComparableAssessmentsUseCase.AssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetComparableAssessmentsRestController {

    private final GetComparableAssessmentsUseCase useCase;

    @GetMapping("/assessments/compare")
    public ResponseEntity<PaginatedResponse<AssessmentListItem>> getComparableAssessments(
        @RequestParam(value = "spaceIds", required = false) List<Long> spaceIds,
        @RequestParam(value = "assessmentKitId", required = false) Long kitId,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "0") int page
    ) {
        var result = useCase.getComparableAssessments(toParam(spaceIds, kitId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetComparableAssessmentsUseCase.Param toParam(List<Long> spaceIds, Long kitId, int size, int page) {
        return new GetComparableAssessmentsUseCase.Param(spaceIds, kitId, size, page);
    }

}
