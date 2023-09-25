package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class GetAssessmentListRestController {

    private final GetAssessmentListUseCase useCase;

    @GetMapping("/assessments")
    public ResponseEntity<PaginatedResponse<AssessmentListItem>> getAssessmentList(
        @RequestParam(value = "spaceId", required = false) // validated in the use-case param
            Long spaceId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        PaginatedResponse<AssessmentListItem> result = useCase.getAssessmentList(toParam(spaceId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAssessmentListUseCase.Param toParam(Long spaceId, int size, int page) {
        return new GetAssessmentListUseCase.Param(spaceId, size, page);
    }
}
