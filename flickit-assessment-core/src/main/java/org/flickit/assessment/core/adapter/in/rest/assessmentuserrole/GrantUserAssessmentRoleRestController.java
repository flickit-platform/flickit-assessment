package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GrantUserAssessmentRoleRestController {

    private final GrantUserAssessmentRoleUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/assessment-user-roles")
    public ResponseEntity<Void> grantAssessmentUserRole(@PathVariable("assessmentId") UUID assessmentId,
                                                        @RequestBody GrantUserAssessmentRoleRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.grantAssessmentUserRole(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, GrantUserAssessmentRoleRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.userId(), requestDto.roleId(), currentUserId);
    }
}
