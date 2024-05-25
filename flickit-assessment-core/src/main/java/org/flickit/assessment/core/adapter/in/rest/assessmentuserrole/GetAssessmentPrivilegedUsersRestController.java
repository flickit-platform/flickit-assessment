package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentPrivilegedUsersUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentPrivilegedUsersUseCase.AssessmentPrivilegedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentPrivilegedUsersRestController {

    private final GetAssessmentPrivilegedUsersUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/privileged-users")
    public ResponseEntity<PaginatedResponse<AssessmentPrivilegedUser>> getAssessmentPrivilegedUsers(@PathVariable UUID assessmentId,
                                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                                    @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        PaginatedResponse<AssessmentPrivilegedUser> response = useCase.getAssessmentPrivilegedUsers(toParam(assessmentId, currentUserId, size, page));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAssessmentPrivilegedUsersUseCase.Param toParam(UUID assessmentId, UUID currentUserId, int size, int page) {
        return new GetAssessmentPrivilegedUsersUseCase.Param(assessmentId, currentUserId, size, page);
    }
}
