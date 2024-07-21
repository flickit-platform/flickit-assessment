package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentAccessCheckerServiceTest {

    @InjectMocks
    private AssessmentAccessCheckerService service;

    @Mock
    private CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Test
    void testIsAuthorized_UserHasNotAccessToSpace_ReturnFalse() {
        var assessmentId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var permission = AssessmentPermission.VIEW_ASSESSMENT;

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(assessmentId, userId)).thenReturn(false);

        boolean authorized = service.isAuthorized(assessmentId, userId, permission);
        assertFalse(authorized);
    }

    @Test
    void testIsAuthorized_UserHasAccessToSpaceAndHasNotPermission_ReturnFalse() {
        var assessmentId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var permission = AssessmentPermission.VIEW_ASSESSMENT;

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(assessmentId, userId)).thenReturn(true);
        when(assessmentPermissionChecker.isAuthorized(assessmentId, userId, permission)).thenReturn(false);

        boolean authorized = service.isAuthorized(assessmentId, userId, permission);
        assertFalse(authorized);
    }

    @Test
    void testIsAuthorized_UserHasAccessToSpaceAndHasPermission_ReturnTrue() {
        var assessmentId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var permission = AssessmentPermission.VIEW_ASSESSMENT;

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(assessmentId, userId)).thenReturn(true);
        when(assessmentPermissionChecker.isAuthorized(assessmentId, userId, permission)).thenReturn(true);

        boolean authorized = service.isAuthorized(assessmentId, userId, permission);
        assertTrue(authorized);
    }
}
