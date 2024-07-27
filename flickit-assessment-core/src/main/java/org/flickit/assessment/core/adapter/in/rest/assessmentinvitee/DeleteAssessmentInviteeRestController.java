package org.flickit.assessment.core.adapter.in.rest.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.DeleteAssessmentInviteeUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.DeleteAssessmentInviteeUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAssessmentInviteeRestController {

    private final DeleteAssessmentInviteeUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/assessments/invitees/{id}")
    public ResponseEntity<Void> deleteAssessmentInvitees(@PathVariable UUID id) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteInvitees(toParam(id, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }
}
