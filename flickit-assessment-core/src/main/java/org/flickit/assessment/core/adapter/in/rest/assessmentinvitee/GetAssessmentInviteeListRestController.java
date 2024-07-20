package org.flickit.assessment.core.adapter.in.rest.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.GetAssessmentInviteeListUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.GetAssessmentInviteeListUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.GetAssessmentInviteeListUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentInviteeListRestController {

    private final GetAssessmentInviteeListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/invitees")
    public ResponseEntity<PaginatedResponse<Result>> getAssessmentInvitees(@PathVariable UUID assessmentId,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getInvitees(toParam(assessmentId, currentUserId, size, page));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId, int size, int page) {
        return new Param(assessmentId, currentUserId, size, page);
    }
}
