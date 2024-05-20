package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAssessmentRoleServiceTest {

    @InjectMocks
    private GrantUserAssessmentRoleService service;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Test
    void testGrantAssessmentUserRole_CurrentUserRoleIsNull_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkUserAssessmentAccessPort, grantUserAssessmentRolePort);
    }

    @Test
    void testGrantAssessmentUserRole_CurrentUserRoleIsNotManager_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkUserAssessmentAccessPort, grantUserAssessmentRolePort);
    }

    @Test
    void testGrantAssessmentUserRole_UserHasNotAccessToAssessment_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(false);

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER, exception.getMessage());

        verifyNoInteractions(grantUserAssessmentRolePort);
    }

    @Test
    void testGrantAssessmentUserRole_ValidParam_GrantAccess() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(true);

        doNothing().when(grantUserAssessmentRolePort)
            .grantUserAssessmentRole(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        service.grantAssessmentUserRole(param);

        verify(grantUserAssessmentRolePort, times(1))
            .grantUserAssessmentRole(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
