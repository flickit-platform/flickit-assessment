package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.flickit.assessment.common.application.domain.assessment.NotificationType;
import org.flickit.assessment.common.application.domain.assessment.SpaceAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRolePayLoad;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRolePayLoad.AssessmentModel;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRolePayLoad.AssignerModel;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRolePayLoad.RoleModel;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.notification.SendNotificationPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.notification.SendNotificationPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private SendNotificationPort sendNotificationPort;

    @Test
    void testGrantAssessmentUserRole_CurrentUserIsNotAuthorized_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(checkAssessmentSpaceMembershipPort, grantUserAssessmentRolePort, getAssessmentPort, loadUserPort, sendNotificationPort);
    }

    @Test
    void testGrantAssessmentUserRole_UserIsNotSpaceMember_AddUserToSpace() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId())).thenReturn(false);

        doNothing().when(createSpaceUserAccessPort).persist(any());

        service.grantAssessmentUserRole(param);

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
    void testGrantAssessmentUserRole_ValidParam_GrantAccess() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());
        var assessment = AssessmentMother.assessment();
        var current_user = new User(param.getCurrentUserId(), "current user");
        var notificationData = new GrantAssessmentUserRolePayLoad(
            new AssessmentModel(assessment.getTitle()),
            new AssignerModel(current_user.getDisplayName()),
            new RoleModel(AssessmentUserRole.COMMENTER.getTitle())
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId())).thenReturn(true);

        doNothing().when(grantUserAssessmentRolePort)
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadById(param.getCurrentUserId())).thenReturn(Optional.of(current_user));
        doNothing().when(sendNotificationPort)
            .sendNotification(param.getUserId(), NotificationType.GRANT_USER_ASSESSMENT_ROLE, notificationData);

        service.grantAssessmentUserRole(param);

        verify(grantUserAssessmentRolePort, times(1))
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
        verify(sendNotificationPort, times(1))
            .sendNotification(param.getUserId(), NotificationType.GRANT_USER_ASSESSMENT_ROLE, notificationData);
    }
}
