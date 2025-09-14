package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
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
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ROLE_NOT_FOUND;
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
    void testDeleteAssessmentUserRole_whenCurrentUserDoesNotHaveRequiredPermissionAndUserRoleIsNotReportViewer_thenThrowAccessDenied() {
        assessmentUserRoleItem =
            new AssessmentUserRoleItem(param.getAssessmentId(), param.getUserId(), AssessmentUserRole.MANAGER, param.getCurrentUserId(), LocalDateTime.now());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.ofNullable(assessmentUserRoleItem));

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadAssessmentPort,
            loadSpaceOwnerPort,
            deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenUserAssessmentRoleNotFound_thenThrowResourceNotFound() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ROLE_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(loadAssessmentPort,
            loadSpaceOwnerPort,
            deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenCurrentUserDoesNotHaveRequiredPermissionAndIsNotUserInviter_thenThrowAccessDenied() {
        assessmentUserRoleItem =
            new AssessmentUserRoleItem(param.getAssessmentId(), param.getUserId(), AssessmentUserRole.REPORT_VIEWER, UUID.randomUUID(), LocalDateTime.now());

        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.ofNullable(assessmentUserRoleItem));

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadAssessmentPort,
            loadSpaceOwnerPort,
            deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenCurrentUserIsNotSpaceMember_thenThrowAccessDenied() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadUserRoleForAssessmentPort,
            loadSpaceOwnerPort,
            deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenUserIsSpaceOwner_thenThrowValidationException() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId())).thenReturn(param.getUserId());

        var exception = assertThrows(ValidationException.class, () -> service.deleteAssessmentUserRole(param));
        assertEquals(DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER, exception.getMessageKey());

        verifyNoInteractions(loadUserRoleForAssessmentPort, deleteUserAssessmentRolePort);
    }

    @Test
    void testDeleteAssessmentUserRole_whenParametersAreValidAndCurrentUserIsSpaceMember_thenSuccessfulDelete() {
        when(assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            .thenReturn(false);
        when(loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())).thenReturn(Optional.of(assessmentUserRoleItem));
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId())).thenReturn(UUID.randomUUID());

        service.deleteAssessmentUserRole(param);

        verify(deleteUserAssessmentRolePort).delete(param.getAssessmentId(), param.getUserId());
    }

    private DeleteUserAssessmentRoleUseCase.Param createParam(Consumer<DeleteUserAssessmentRoleUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteUserAssessmentRoleUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteUserAssessmentRoleUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
