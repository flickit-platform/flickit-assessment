package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAssessmentRoleServiceTest {

    @InjectMocks
    private GrantUserAssessmentRoleService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private CreateAssessmentSpaceUserAccessPort createSpaceUserAccessPort;

    @Test
    void testGrantUserAssessmentRoleRole_CurrentUserIsNotAuthorized_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(checkAssessmentSpaceMembershipPort, grantUserAssessmentRolePort);
    }

    @Test
    void testGrantUserAssessmentRole_UserIsNotSpaceMember_AddUserToSpace() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());
        var notificationData = new GrantAssessmentUserRoleNotificationCmd(param.getUserId(),
            param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentUserRole.valueOfById(param.getRoleId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId())).thenReturn(false);

        doNothing().when(createSpaceUserAccessPort).persist(any());

        var result = service.grantAssessmentUserRole(param);

        GrantAssessmentUserRoleNotificationCmd cmd = (GrantAssessmentUserRoleNotificationCmd) result.notificationCmd();
        assertEquals(notificationData.targetUserId(), cmd.targetUserId());
        assertEquals(notificationData.assignerUserId(), cmd.assignerUserId());
        assertEquals(notificationData.assessmentId(), cmd.assessmentId());

        ArgumentCaptor<CreateAssessmentSpaceUserAccessPort.Param> createSpaceUserAccessPortParam =
            ArgumentCaptor.forClass(CreateAssessmentSpaceUserAccessPort.Param.class);
        verify(createSpaceUserAccessPort, times(1)).persist(createSpaceUserAccessPortParam.capture());

        assertEquals(param.getAssessmentId(), createSpaceUserAccessPortParam.getValue().assessmentId());
        assertEquals(param.getUserId(), createSpaceUserAccessPortParam.getValue().userId());
        assertEquals(param.getCurrentUserId(), createSpaceUserAccessPortParam.getValue().createdBy());
        assertNotNull(createSpaceUserAccessPortParam.getValue().creationTime());

        verify(grantUserAssessmentRolePort, times(1))
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
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

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId())).thenReturn(true);

        doNothing().when(grantUserAssessmentRolePort)
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        var result = service.grantAssessmentUserRole(param);

        GrantAssessmentUserRoleNotificationCmd cmd = (GrantAssessmentUserRoleNotificationCmd) result.notificationCmd();
        assertEquals(notificationData.targetUserId(), cmd.targetUserId());
        assertEquals(notificationData.assignerUserId(), cmd.assignerUserId());
        assertEquals(notificationData.assessmentId(), cmd.assessmentId());

        verify(grantUserAssessmentRolePort, times(1))
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
