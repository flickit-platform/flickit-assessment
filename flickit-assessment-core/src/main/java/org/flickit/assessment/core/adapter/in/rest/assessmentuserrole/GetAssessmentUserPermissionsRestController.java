package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserPermissionsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentUserPermissionsRestController {

    private final GetAssessmentUserPermissionsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/permissions")
    public ResponseEntity<Map<String, Boolean>> getAssessmentUserPermissions(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var permissions = useCase.getAssessmentUserPermissions(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    private GetAssessmentUserPermissionsUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new GetAssessmentUserPermissionsUseCase.Param(assessmentId, currentUserId);
    }
}
