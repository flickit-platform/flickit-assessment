package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GetSpaceAssessmentListUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetSpaceAssessmentListUseCase.SpaceAssessmentListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSpaceAssessmentListRestController {

    private final GetSpaceAssessmentListUseCase spaceAssessmentListUseCase;
    private final UserContext userContext;

    @GetMapping("/space-assessments")
    public ResponseEntity<PaginatedResponse<SpaceAssessmentListItem>> getSpaceAssessmentList(
        @RequestParam(value = "spaceId", required = false) Long spaceId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {

        UUID currentUserId = userContext.getUser().id();
        PaginatedResponse<SpaceAssessmentListItem> result =
            spaceAssessmentListUseCase.getAssessmentList(toSpaceAssessmentsParam(spaceId, currentUserId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetSpaceAssessmentListUseCase.Param toSpaceAssessmentsParam(Long spaceId, UUID currentUserId, int size, int page) {
        return new GetSpaceAssessmentListUseCase.Param(spaceId, currentUserId, size, page);
    }
}
