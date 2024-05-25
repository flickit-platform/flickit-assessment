package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.DeleteUserAssessmentRolePort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAssessmentRoleServiceTest {

    @InjectMocks
    DeleteUserAssessmentRoleService service;

    @Mock
    AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    DeleteUserAssessmentRolePort deleteUserAssessmentRolePort;

    @Test
    @DisplayName("Deleting an assessment permission role should be done by a user with the 'Manager' role.")
    void testDeleteAssessmentUserRole_nonManagerUser_AccessDenied() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        DeleteUserAssessmentRoleUseCase.Param param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId,
            userId,
            currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkUserAssessmentAccessPort, deleteUserAssessmentRolePort);
    }

    @Test
    @DisplayName("Deleting an assessment permission role should be done on a user that has already access to assessment.")
    void testDeleteAssessmentUserRole_userHasNotAccess_ResourceNotFound() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        DeleteUserAssessmentRoleUseCase.Param param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId,
            userId,
            currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, userId)).thenReturn(false);

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(DELETE_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER, exception.getMessage());

        verify(assessmentPermissionChecker).isAuthorized(assessmentId, currentUserId, DELETE_USER_ASSESSMENT_ROLE);
        verifyNoInteractions(deleteUserAssessmentRolePort);
    }

    @Test
    @DisplayName("Deleting an assessment permission role with valid parameters should be successful")
    void testDeleteAssessmentUserRole_validParameters_Successful() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        DeleteUserAssessmentRoleUseCase.Param param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId,
            userId,
            currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, userId)).thenReturn(true);

        assertDoesNotThrow(()-> service.deleteAssessmentUserRole(param));

        verify(assessmentPermissionChecker).isAuthorized(assessmentId, currentUserId, DELETE_USER_ASSESSMENT_ROLE);
        verify(deleteUserAssessmentRolePort).deleteUserAssessmentRole(param.getAssessmentId(), param.getUserId());
    }
}
