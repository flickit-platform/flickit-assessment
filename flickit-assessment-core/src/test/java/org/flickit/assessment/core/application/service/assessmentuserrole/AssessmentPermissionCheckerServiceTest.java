package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentPermissionCheckerServiceTest {

    @InjectMocks
    private AssessmentPermissionCheckerService service;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Test
    void testIsAuthorized_userHasNotAnyRole_shouldReturnFalse() {
        Assessment assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var userId = UUID.randomUUID();

        when(loadUserRoleForAssessmentPort.load(assessmentId, userId)).thenReturn(null);

        AssessmentUserRole.VIEWER.getPermissions()
            .forEach(x ->
                assertFalse(service.isAuthorized(assessmentId, userId, x))
            );
    }

    @Test
    void testIsAuthorized_userHasViewerRole_shouldReturnValidResult() {
        Assessment assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var userId = UUID.randomUUID();

        when(loadUserRoleForAssessmentPort.load(assessmentId, userId)).thenReturn(AssessmentUserRole.VIEWER);

        AssessmentUserRole.VIEWER.getPermissions()
            .forEach(x ->
                assertTrue(service.isAuthorized(assessmentId, userId, x))
            );

        Set<AssessmentPermission> managerPermissions = new HashSet<>(AssessmentUserRole.MANAGER.getPermissions());
        managerPermissions.removeAll(AssessmentUserRole.ASSESSOR.getPermissions());
        managerPermissions
            .forEach(x ->
                assertFalse(service.isAuthorized(assessmentId, userId, x))
            );
    }
}
