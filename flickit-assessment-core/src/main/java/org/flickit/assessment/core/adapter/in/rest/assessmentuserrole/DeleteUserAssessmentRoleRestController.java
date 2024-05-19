package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteUserAssessmentRoleRestController {

    private final DeleteUserAssessmentRoleUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/assessments/{assessmentId}/assessment-user-roles")
    public ResponseEntity<Void> deleteAssessmentUserRole(@PathVariable("assessmentId") UUID assessmentId,
                                                        @RequestBody DeleteUserAssessmentRoleRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAssessmentUserRole(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, DeleteUserAssessmentRoleRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.userId(), requestDto.roleId(), currentUserId);
    }
}
