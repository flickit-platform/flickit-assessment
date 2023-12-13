package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class GetAssessmentListRestController {

    private final GetAssessmentListUseCase useCase;

    @GetMapping("/assessments")
    public ResponseEntity<PaginatedResponse<AssessmentListItem>> getAssessmentList(
        @RequestParam(value = "spaceIds", required = false) // validated in the use-case param
        List<Long> spaceIds,
        @RequestParam(value = "kitId", required = false) Long kitId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        PaginatedResponse<AssessmentListItem> result = useCase.getAssessmentList(toParam(spaceIds, kitId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAssessmentListUseCase.Param toParam(List<Long> spaceIds, Long kitId, int size, int page) {
        return new GetAssessmentListUseCase.Param(spaceIds, kitId, size, page);
    }
}
