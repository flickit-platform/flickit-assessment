package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.UpdateUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.UpdateUserAssessmentRoleUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateUserAssessmentRoleRestController {

    private final UpdateUserAssessmentRoleUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/assessment-user-roles")
    public ResponseEntity<Void> updateAssessmentUserRole(@PathVariable("assessmentId") UUID assessmentId,
                                                         @RequestBody UpdateUserAssessmentRoleRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAssessmentUserRole(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UpdateUserAssessmentRoleRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.userId(), requestDto.roleId(), currentUserId);
    }
}
