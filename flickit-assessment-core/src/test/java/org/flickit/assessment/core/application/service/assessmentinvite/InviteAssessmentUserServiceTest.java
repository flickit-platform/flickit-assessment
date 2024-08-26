package org.flickit.assessment.core.application.service.assessmentinvite;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessmentinvite.InviteAssessmentUserUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.CreateAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
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
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CreateSpaceInvitePort createSpaceInvitePort;

    @Mock
    private CreateAssessmentInvitePort createAssessmentInvitePort;

    @Mock
    private AppSpecProperties appSpecProperties;

    @Mock
    private SendEmailPort sendEmailPort;

    @Mock
    GrantUserAssessmentRolePort grantUserAssessmentRolePort;

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

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenThrow(new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.inviteUser(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verifyNoInteractions(loadUserPort, createSpaceInvitePort,
            createAssessmentInvitePort, sendEmailPort, grantUserAssessmentRolePort, checkSpaceAccessPort);
    }

    @Test
    @DisplayName("If current user doesn't have required permission, the service should throw an AccessDeniedException.")
    void testInviteAssessmentUser_InviterIsNotManager_AccessDeniedException() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteUser(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(getAssessmentPort, loadUserPort, createSpaceInvitePort,
            createAssessmentInvitePort, sendEmailPort, grantUserAssessmentRolePort, checkSpaceAccessPort);
    }

    @Test
    @DisplayName("If the user is not registered, the service should create space and assessment invitations and send an invite email.")
    void testInviteAssessmentUser_ValidParametersNotRegisteredUser_SuccessfulInviteePersist() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        doNothing().when(createSpaceInvitePort).persist(isA(CreateSpaceInvitePort.Param.class));
        doNothing().when(createAssessmentInvitePort).persist(isA(CreateAssessmentInvitePort.Param.class));
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE);
        verify(createSpaceInvitePort).persist(any(CreateSpaceInvitePort.Param.class));
        verify(createAssessmentInvitePort).persist(any(CreateAssessmentInvitePort.Param.class));
        verify(appSpecProperties, times(2)).getName();
        verify(appSpecProperties).getHost();
        verify(sendEmailPort).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("If the user is registered and already a member of the related space, the user should be granted the assessment role.")
    void testInviteAssessmentUser_ValidParametersRegisteredUserIsInSpace_SuccessfulGrantAccess() {
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var user = new User(UUID.randomUUID(), "Display Name");

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(Optional.of(user));
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())).thenReturn(true);
        doNothing().when(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE);
        verify(checkSpaceAccessPort).checkIsMember(assessment.getSpace().getId(), user.getId());
        verify(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());

        verifyNoInteractions(sendEmailPort);
    }

    @Test
    @DisplayName("If the user is registered but not a member of the related space, the user should be granted the assessment role, and access to the space should be created as well.")
    void testInviteAssessmentUser_ValidParametersRegisteredUserIsNotInSpace_SuccessfulGrantAccess() {
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var user = new User(UUID.randomUUID(), "Display Name");
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(Optional.of(user));
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())).thenReturn(false);
        doNothing().when(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());
        doNothing().when(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE);
        verify(checkSpaceAccessPort).checkIsMember(assessment.getSpace().getId(), user.getId());
        verify(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());
        verify(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));
        verifyNoInteractions(sendEmailPort);
    }
}
