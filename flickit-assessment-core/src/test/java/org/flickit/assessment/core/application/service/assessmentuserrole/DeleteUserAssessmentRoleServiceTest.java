package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.DeleteUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAssessmentRoleServiceTest {

    @InjectMocks
    DeleteUserAssessmentRoleService service;

    @Mock
    AssessmentPermissionChecker assessmentPermissionChecker;

    @Mock
    CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;

    @Mock
    DeleteUserAssessmentRolePort deleteUserAssessmentRolePort;

    @Mock
    private LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Test
    @DisplayName("Deleting an assessment user role should be done only by a user with the required permission")
    void testDeleteAssessmentUserRole_currentUserDoesNotHaveRequiredPermission_AccessDenied() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId, userId, currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkAssessmentSpaceMembershipPort, deleteUserAssessmentRolePort);
    }

    @Test
    @DisplayName("Deleting an assessment user role should be done only by a user that is member of related space and with the required permission")
    void testDeleteAssessmentUserRole_currentUserIsNotSpaceMember_AccessDenied() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId, userId, currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(assessmentId, currentUserId)).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verify(assessmentPermissionChecker).isAuthorized(assessmentId, currentUserId, DELETE_USER_ASSESSMENT_ROLE);
        verifyNoInteractions(deleteUserAssessmentRolePort);
    }

    @Test
    @DisplayName("Deleting an assessment user role should not be done if user is owner of assessment's space")
    void testUpdateAssessmentUserRole_UserIsSpaceOwner_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId, userId, currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId())).thenReturn(param.getUserId());

        var exception = assertThrows(ValidationException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER, exception.getMessageKey());

        verifyNoInteractions(deleteUserAssessmentRolePort);
    }

    @Test
    @DisplayName("Deleting an assessment permission role with valid parameters should be successful")
    void testDeleteAssessmentUserRole_validParameters_Successful() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteUserAssessmentRoleUseCase.Param(assessmentId, userId, currentUserId);

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(assessmentId, currentUserId)).thenReturn(true);

        assertDoesNotThrow(()-> service.deleteAssessmentUserRole(param));

        verify(assessmentPermissionChecker).isAuthorized(assessmentId, currentUserId, DELETE_USER_ASSESSMENT_ROLE);
        verify(deleteUserAssessmentRolePort).delete(param.getAssessmentId(), param.getUserId());
    }
}
