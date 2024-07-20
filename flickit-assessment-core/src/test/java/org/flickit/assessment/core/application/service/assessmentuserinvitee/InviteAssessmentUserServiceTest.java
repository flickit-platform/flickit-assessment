package org.flickit.assessment.core.application.service.assessmentuserinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase.*;
import org.flickit.assessment.core.application.port.mail.SendFlickitInviteMailPort;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.InviteSpaceMemberPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteAssessmentUserServiceTest {

    @InjectMocks
    private InviteAssessmentUserService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Mock
    private InviteSpaceMemberPort inviteSpaceMemberPort;

    @Mock
    private InviteAssessmentUserPort inviteAssessmentUserPort;

    @Mock
    private SendFlickitInviteMailPort sendFlickitInviteMailPort;

    @Mock
    GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    CreateAssessmentSpaceUserAccessPort createAssessmentSpaceUserAccessPort;

    @Test
    @DisplayName("If the assessment does not exist, the service should throw a notFoundException.")
    void testInviteAssessmentUser_AssessmentDoesNotExist_ResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenThrow(new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.inviteUser(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verifyNoInteractions(loadUserRoleForAssessmentPort, loadUserPort, inviteSpaceMemberPort,
            inviteAssessmentUserPort, sendFlickitInviteMailPort, grantUserAssessmentRolePort,
            checkAssessmentSpaceMembershipPort, checkSpaceAccessPort);
    }

    @Test
    @DisplayName("If the inviter is not a manager on this assessment, the invitation will fail.")
    void testInviteAssessmentUser_InviterIsNotManager_AccessDenied() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(AssessmentMother.assessment()));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.VIEWER));

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteUser(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserRoleForAssessmentPort).load(param.getAssessmentId(), param.getCurrentUserId());
        verifyNoInteractions(loadUserPort, inviteSpaceMemberPort,
            inviteAssessmentUserPort, sendFlickitInviteMailPort, grantUserAssessmentRolePort,
            checkAssessmentSpaceMembershipPort, checkSpaceAccessPort);
    }

    @Test
    @DisplayName("If input parameters are valid, and the user is not registered previously, the service should save the invitee record.")
    void testInviteAssessmentUser_ValidParametersNotRegisteredUser_SuccessfulInviteePersist() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(Optional.empty());
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        doNothing().when(inviteSpaceMemberPort).invite(isA(InviteSpaceMemberPort.Param.class));
        doNothing().when(inviteAssessmentUserPort).invite(isA(InviteAssessmentUserPort.Param.class));
        doNothing().when(sendFlickitInviteMailPort).inviteToFlickit(isA(String.class));

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(loadUserRoleForAssessmentPort).load(param.getAssessmentId(), param.getCurrentUserId());
        verify(inviteSpaceMemberPort).invite(any(InviteSpaceMemberPort.Param.class));
        verify(inviteAssessmentUserPort).invite(any(InviteAssessmentUserPort.Param.class));
        verify(sendFlickitInviteMailPort).inviteToFlickit(email);
    }

    @Test
    @DisplayName("If input parameters are valid, and the user is registered previously and is in space, the service should save the role for assessment.")
    void testInviteAssessmentUser_ValidParametersRegisteredUserIsInSpace_SuccessfulInviteePersist() {
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var user = new User(UUID.randomUUID(), "Display Name");

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(Optional.of(user));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())).thenReturn(true);
        doNothing().when(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(loadUserRoleForAssessmentPort).load(param.getAssessmentId(), param.getCurrentUserId());
        verify(checkSpaceAccessPort).checkIsMember(assessment.getSpace().getId(), user.getId());
        verify(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());

        verifyNoInteractions(sendFlickitInviteMailPort);
    }

    @Test
    @DisplayName("If input parameters are valid, and the user is registered previously and is not in the space, the service should save the role for assessment.")
    void testInviteAssessmentUser_ValidParametersRegisteredUserIsNotInSpace_SuccessfulInviteePersist() {
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var user = new User(UUID.randomUUID(), "Display Name");
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(Optional.of(user));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())).thenReturn(false);
        doNothing().when(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());
        when(checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), user.getId())).thenReturn(false);
        doNothing().when(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(loadUserRoleForAssessmentPort).load(param.getAssessmentId(), param.getCurrentUserId());
        verify(checkSpaceAccessPort).checkIsMember(assessment.getSpace().getId(), user.getId());
        verify(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());
        verify(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));
        verifyNoInteractions(sendFlickitInviteMailPort);
    }
}
