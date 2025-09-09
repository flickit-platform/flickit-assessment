package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.DeleteUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

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
    LoadAssessmentPort loadAssessmentPort;

    @Mock
    DeleteUserAssessmentRolePort deleteUserAssessmentRolePort;

    @Mock
    private LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    private final DeleteUserAssessmentRoleUseCase.Param param = createParam(p -> DeleteUserAssessmentRoleUseCase.Param.builder());
    private AssessmentUserRoleItem assessmentUserRoleItem =
        new AssessmentUserRoleItem(param.getAssessmentId(), param.getUserId(), AssessmentUserRole.REPORT_VIEWER, param.getCurrentUserId(), LocalDateTime.now());

    @Test
    void testDeleteAssessmentUserRole_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDenied() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadUserRoleForAssessmentPort,
            loadAssessmentPort,
            deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenCurrentUserIsNotSpaceMemberAndIsNotInviter_thenThrowAccessDenied() {
        assessmentUserRoleItem = new AssessmentUserRoleItem(param.getAssessmentId(), param.getUserId(), AssessmentUserRole.REPORT_VIEWER, UUID.randomUUID(), LocalDateTime.now());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.of(assessmentUserRoleItem));
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenRoleIsNotReportViewer_thenThrowAccessDenied() {
        assessmentUserRoleItem = new AssessmentUserRoleItem(param.getAssessmentId(), param.getUserId(), AssessmentUserRole.ASSESSOR, param.getCurrentUserId(), LocalDateTime.now());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.of(assessmentUserRoleItem));
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verify(assessmentPermissionChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE);
        verifyNoInteractions(deleteUserAssessmentRolePort);
    }

    @Test
    void testUpdateAssessmentUserRole_whenUserIsSpaceOwner_thenThrowValidationException() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId())).thenReturn(param.getUserId());

        var exception = assertThrows(ValidationException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER, exception.getMessageKey());

        verifyNoInteractions(deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenParametersAreValidAndCurrentUserIsSpaceMember_thenSuccessfulDelete() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteAssessmentUserRole(param));

        verify(assessmentPermissionChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE);
        verify(deleteUserAssessmentRolePort).delete(param.getAssessmentId(), param.getUserId());
    }

    @Test
    void testDeleteAssessmentUserRole_whenParametersAreValidAndCurrentUserIsNotSpaceMemberButInviter_thenSuccessfulDelete() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(false);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.of(assessmentUserRoleItem));

        assertDoesNotThrow(() -> service.deleteAssessmentUserRole(param));

        verify(assessmentPermissionChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE);
        verify(deleteUserAssessmentRolePort).delete(param.getAssessmentId(), param.getUserId());
    }

    private DeleteUserAssessmentRoleUseCase.Param createParam(Consumer<DeleteUserAssessmentRoleUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private DeleteUserAssessmentRoleUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteUserAssessmentRoleUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
