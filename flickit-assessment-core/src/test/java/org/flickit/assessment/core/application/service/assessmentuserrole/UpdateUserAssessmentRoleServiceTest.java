package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.permission.AssessmentPermissionChecker;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.UpdateUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.UpdateUserAssessmentRolePort;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.permission.AssessmentPermission.UPDATE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserAssessmentRoleServiceTest {

    @InjectMocks
    private UpdateUserAssessmentRoleService service;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private UpdateUserAssessmentRolePort updateUserAssessmentRolePort;

    @Test
    void testUpdateAssessmentUserRole_CurrentUserRoleIsNull_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkUserAssessmentAccessPort, updateUserAssessmentRolePort);
    }

    @Test
    void testUpdateAssessmentUserRole_CurrentUserRoleIsNotManager_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkUserAssessmentAccessPort, updateUserAssessmentRolePort);
    }

    @Test
    void testUpdateAssessmentUserRole_UserHasNotAccessToAssessment_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(false);

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(UPDATE_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER, exception.getMessage());

        verifyNoInteractions(updateUserAssessmentRolePort);
    }

    @Test
    void testUpdateAssessmentUserRole_AssessmentIdUserIdNotExist_ResourceNotFound() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(true);

        doThrow(ResourceNotFoundException.class).when(updateUserAssessmentRolePort)
            .updateUserAssessmentRole(param.getAssessmentId(), param.getUserId(),param.getRoleId());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAssessmentUserRole(param));


        verify(updateUserAssessmentRolePort, times(1))
            .updateUserAssessmentRole(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }

    @Test
    void testUpdateAssessmentUserRole_ValidParam_UpdateAccess() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(true);

        doNothing().when(updateUserAssessmentRolePort)
            .updateUserAssessmentRole(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        service.updateAssessmentUserRole(param);

        verify(updateUserAssessmentRolePort, times(1))
            .updateUserAssessmentRole(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
