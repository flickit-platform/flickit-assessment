package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteUserAssessmentRoleRestController {

    private final DeleteUserAssessmentRoleUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/assessments/{assessmentId}/assessment-user-roles/{userId}")
    public ResponseEntity<Void> deleteAssessmentUserRole(@PathVariable("assessmentId") UUID assessmentId,
                                                         @PathVariable("userId") UUID userId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAssessmentUserRole(toParam(assessmentId, userId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(UUID assessmentId, UUID userId, UUID currentUserId) {
        return new Param(assessmentId, userId, currentUserId);
    }
}
