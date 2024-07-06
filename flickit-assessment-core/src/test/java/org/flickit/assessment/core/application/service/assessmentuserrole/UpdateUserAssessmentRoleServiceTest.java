package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.UpdateUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.UpdateUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER;
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

    @Mock
    private LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Test
    void testUpdateAssessmentUserRole_CurrentUserDoesNotHaveRequiredRole_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(checkUserAssessmentAccessPort, updateUserAssessmentRolePort, loadSpaceOwnerPort);
    }

    @Test
    void testUpdateAssessmentUserRole_CurrentUserIsNotSpaceMember_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateUserAssessmentRolePort, loadSpaceOwnerPort);
    }

    @Test
    void testUpdateAssessmentUserRole_UserIsNotSpaceMember_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(UPDATE_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER, exception.getMessageKey());

        verifyNoInteractions(updateUserAssessmentRolePort, loadSpaceOwnerPort);
    }

    @Test
    void testUpdateAssessmentUserRole_UserIsSpaceOwner_ThrowsException() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId())).thenReturn(param.getUserId());

        var exception = assertThrows(ValidationException.class, () -> service.updateAssessmentUserRole(param));
        assertEquals(UPDATE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER, exception.getMessageKey());

        verifyNoInteractions(updateUserAssessmentRolePort);
    }

    @Test
    void testUpdateAssessmentUserRole_ValidParam_UpdateAccess() {
        Param param = new Param(UUID.randomUUID(),
            UUID.randomUUID(),
            1,
            UUID.randomUUID());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            .thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId())).thenReturn(UUID.randomUUID());

        doNothing().when(updateUserAssessmentRolePort)
            .update(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        service.updateAssessmentUserRole(param);

        verify(updateUserAssessmentRolePort, times(1))
            .update(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
