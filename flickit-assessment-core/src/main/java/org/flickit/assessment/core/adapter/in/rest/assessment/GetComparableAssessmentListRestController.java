package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GetComparableAssessmentListUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetComparableAssessmentListUseCase.ComparableAssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetComparableAssessmentListRestController {

    private final GetComparableAssessmentListUseCase assessmentListUseCase;
    private final UserContext userContext;

    @GetMapping("/comparable-assessments")
    public ResponseEntity<PaginatedResponse<ComparableAssessmentListItem>> getComparableAssessmentList(
        @RequestParam(value = "kitId", required = false) Long kitId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        var result = assessmentListUseCase.getComparableAssessmentList(toUserAssessmentParam(kitId, currentUserId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetComparableAssessmentListUseCase.Param toUserAssessmentParam(Long kitId, UUID currentUserId, int size, int page) {
        return new GetComparableAssessmentListUseCase.Param(kitId, currentUserId, size, page);
    }
}
