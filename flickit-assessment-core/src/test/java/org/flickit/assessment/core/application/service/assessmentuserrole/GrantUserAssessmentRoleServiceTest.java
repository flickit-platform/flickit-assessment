package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRoleNotificationCmd;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_DEFAULT_SPACE_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAssessmentRoleServiceTest {

    @InjectMocks
    private GrantUserAssessmentRoleService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Captor
    private ArgumentCaptor<AssessmentUserRoleItem> roleItemArgumentCaptor;

    @Test
    void testGrantUserAssessmentRoleRole_whenCurrentUserIsNotAuthorized_thenThrowsException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(grantUserAssessmentRolePort,
            loadAssessmentPort);
    }

    @Test
    void testGrantUserAssessmentRole_whenAssessmentSpaceIsDefault_thenThrowValidationException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isInDefaultSpace(param.getAssessmentId()))
            .thenReturn(true);

        var exception = assertThrows(ValidationException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(GRANT_ASSESSMENT_USER_ROLE_DEFAULT_SPACE_NOT_ALLOWED, exception.getMessageKey());

        verifyNoInteractions(createSpaceUserAccessPort,
            grantUserAssessmentRolePort);
    }

    @Test
    void testGrantUserAssessmentRole_whenUserIsNotSpaceMember_thenAddUserToSpace() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());
        var notificationData = new GrantAssessmentUserRoleNotificationCmd(param.getUserId(),
            param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentUserRole.valueOfById(param.getRoleId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isInDefaultSpace(param.getAssessmentId()))
            .thenReturn(false);
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId()))
            .thenReturn(false);

        doNothing().when(createSpaceUserAccessPort).persistByAssessmentId(any());

        var result = service.grantAssessmentUserRole(param);

        GrantAssessmentUserRoleNotificationCmd cmd = result.notificationCmd();
        assertEquals(notificationData.targetUserId(), cmd.targetUserId());
        assertEquals(notificationData.assignerUserId(), cmd.assignerUserId());
        assertEquals(notificationData.assessmentId(), cmd.assessmentId());

        ArgumentCaptor<CreateSpaceUserAccessPort.CreateParam> createSpaceUserAccessPortParam =
            ArgumentCaptor.forClass(CreateSpaceUserAccessPort.CreateParam.class);
        verify(createSpaceUserAccessPort, times(1)).persistByAssessmentId(createSpaceUserAccessPortParam.capture());

        assertEquals(param.getAssessmentId(), createSpaceUserAccessPortParam.getValue().assessmentId());
        assertEquals(param.getUserId(), createSpaceUserAccessPortParam.getValue().userId());
        assertEquals(param.getCurrentUserId(), createSpaceUserAccessPortParam.getValue().createdBy());
        assertNotNull(createSpaceUserAccessPortParam.getValue().creationTime());

        verify(grantUserAssessmentRolePort, times(1)).persist(roleItemArgumentCaptor.capture());
        assertEquals(param.getAssessmentId(), roleItemArgumentCaptor.getValue().getAssessmentId());
        assertEquals(param.getUserId(), roleItemArgumentCaptor.getValue().getUserId());
        assertEquals(param.getRoleId(), roleItemArgumentCaptor.getValue().getRole().getId());
        assertEquals(param.getCurrentUserId(), roleItemArgumentCaptor.getValue().getCreatedBy());
        assertNotNull(roleItemArgumentCaptor.getValue().getCreationTime());
    }

    @Test
    void testGrantUserAssessmentRole_ValidParam_GrantAccess() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());
        var notificationData = new GrantAssessmentUserRoleNotificationCmd(param.getUserId(),
            param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentUserRole.valueOfById(param.getRoleId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);
        when(loadAssessmentPort.isInDefaultSpace(param.getAssessmentId()))
            .thenReturn(false);
        when(loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId()))
            .thenReturn(true);

        var result = service.grantAssessmentUserRole(param);

        GrantAssessmentUserRoleNotificationCmd cmd = result.notificationCmd();
        assertEquals(notificationData.targetUserId(), cmd.targetUserId());
        assertEquals(notificationData.assignerUserId(), cmd.assignerUserId());
        assertEquals(notificationData.assessmentId(), cmd.assessmentId());

        verify(grantUserAssessmentRolePort, times(1)).persist(roleItemArgumentCaptor.capture());
        assertEquals(param.getAssessmentId(), roleItemArgumentCaptor.getValue().getAssessmentId());
        assertEquals(param.getUserId(), roleItemArgumentCaptor.getValue().getUserId());
        assertEquals(param.getRoleId(), roleItemArgumentCaptor.getValue().getRole().getId());
        assertEquals(param.getCurrentUserId(), roleItemArgumentCaptor.getValue().getCreatedBy());
        assertNotNull(roleItemArgumentCaptor.getValue().getCreationTime());
    }
}
