package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentListRestController {

    private final GetAssessmentListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments")
    public ResponseEntity<PaginatedResponse<AssessmentListItem>> getAssessmentList(
        @RequestParam(value = "spaceIds", required = false)
        List<Long> spaceIds,
        @RequestParam(value = "kitId", required = false) Long kitId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {

        UUID currentUserId = userContext.getUser().id();
        PaginatedResponse<AssessmentListItem> result = useCase.getAssessmentList(toParam(spaceIds, kitId, currentUserId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAssessmentListUseCase.Param toParam(List<Long> spaceIds, Long kitId, UUID currentUserId, int size, int page) {
        return new GetAssessmentListUseCase.Param(spaceIds, kitId, currentUserId, size, page);
    }
}
