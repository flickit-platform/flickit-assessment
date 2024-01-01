package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitListRestController {

    private final GetAssessmentKitListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits")
    public ResponseEntity<PaginatedResponse<GetAssessmentKitListUseCase.AssessmentKitListItem>> getKitList(
        @RequestParam("isPrivate") Boolean isPrivate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        var currentUserId = userContext.getUser().id();
        var assessmentKitList = useCase.getAssessmentKitList(toParam(isPrivate, page, size, currentUserId));
        return new ResponseEntity<>(assessmentKitList, HttpStatus.OK);
    }

    private GetAssessmentKitListUseCase.Param toParam(Boolean isPrivate, int page, int size, UUID currentUserId) {
        return new GetAssessmentKitListUseCase.Param(isPrivate, page, size, currentUserId);
    }
}
