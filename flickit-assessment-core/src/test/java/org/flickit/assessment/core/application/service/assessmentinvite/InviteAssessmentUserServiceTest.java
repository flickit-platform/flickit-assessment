package org.flickit.assessment.core.application.service.assessmentinvite;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessmentinvite.InviteAssessmentUserUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.CreateAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
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
    private SendEmailPort sendEmailPort;

    @Mock
    GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    CreateAssessmentSpaceUserAccessPort createAssessmentSpaceUserAccessPort;

    @Spy // @Spy added for injecting this field in #service
    AppSpecProperties appSpecProperties = appSpecProperties();

    @Test
    void testInviteAssessmentUser_AssessmentDoesNotExist_ResourceNotFoundException() {
        var param = createParam(InviteAssessmentUserUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenThrow(new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.inviteUser(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadUserPort, createSpaceInvitePort,
            createAssessmentInvitePort, sendEmailPort, grantUserAssessmentRolePort, checkSpaceAccessPort);
    }

    @Test
    void testInviteAssessmentUser_CurrentUserDoesNotHaveRequiredPermission_AccessDeniedException() {
        var param = createParam(InviteAssessmentUserUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteUser(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(getAssessmentPort, loadUserPort, createSpaceInvitePort,
            createAssessmentInvitePort, sendEmailPort, grantUserAssessmentRolePort, checkSpaceAccessPort);
    }

    @Test
    void testInviteAssessmentUser_ValidParametersNotRegisteredUser_CreateInvitationsToSpaceAndAssessment() {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        doNothing().when(createSpaceInvitePort).persist(any());
        doNothing().when(createAssessmentInvitePort).persist(any());
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        service.inviteUser(param);

        ArgumentCaptor<CreateSpaceInvitePort.Param> spaceInvitationParamCaptor = ArgumentCaptor.forClass(CreateSpaceInvitePort.Param.class);
        verify(createSpaceInvitePort).persist(spaceInvitationParamCaptor.capture());
        assertEquals(assessment.getSpace().getId(), spaceInvitationParamCaptor.getValue().spaceId());
        assertEquals(param.getEmail(), spaceInvitationParamCaptor.getValue().email());
        assertNotNull(spaceInvitationParamCaptor.getValue().creationTime());
        assertEquals(spaceInvitationParamCaptor.getValue().creationTime().plusDays(7), spaceInvitationParamCaptor.getValue().expirationDate());
        assertEquals(param.getCurrentUserId(), spaceInvitationParamCaptor.getValue().createdBy());

        ArgumentCaptor<CreateAssessmentInvitePort.Param> assessmentInvitationParamCaptor = ArgumentCaptor.forClass(CreateAssessmentInvitePort.Param.class);
        verify(createAssessmentInvitePort).persist(assessmentInvitationParamCaptor.capture());
        assertEquals(param.getAssessmentId(), assessmentInvitationParamCaptor.getValue().assessmentId());
        assertEquals(param.getEmail(), assessmentInvitationParamCaptor.getValue().email());
        assertEquals(param.getRoleId(), assessmentInvitationParamCaptor.getValue().roleId());
        assertNotNull(assessmentInvitationParamCaptor.getValue().creationTime());
        assertEquals(assessmentInvitationParamCaptor.getValue().creationTime().plusDays(7), assessmentInvitationParamCaptor.getValue().expirationTime());
        assertEquals(param.getCurrentUserId(), assessmentInvitationParamCaptor.getValue().createdBy());

        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY,
            appSpecProperties.getHost(),
            appSpecProperties.getName(),
            appSpecProperties.getSupportEmail());
        verify(sendEmailPort).send(param.getEmail(), subject, body);
    }

    @Test
    void testInviteAssessmentUser_ValidParametersNotRegisteredUser_SendInvitationWithoutSupportEmail() {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        doNothing().when(createSpaceInvitePort).persist(any());
        doNothing().when(createAssessmentInvitePort).persist(any());
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        appSpecProperties.setSupportEmail(" ");

        service.inviteUser(param);

        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL,
            appSpecProperties.getHost(),
            appSpecProperties.getName());
        verify(sendEmailPort).send(param.getEmail(), subject, body);
    }

    @Test
    void testInviteAssessmentUser_ValidParametersRegisteredUserIsInSpace_SuccessfulGrantAccess() {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));
        var user = new User(UUID.randomUUID(), "Display Name", "user@mail.com");

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(user));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())).thenReturn(true);
        doNothing().when(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());

        service.inviteUser(param);

        verifyNoInteractions(sendEmailPort, createAssessmentInvitePort, createSpaceInvitePort,
            createAssessmentSpaceUserAccessPort);
    }

    @Test
    void testInviteAssessmentUser_ValidParametersRegisteredUserIsNotInSpace_SuccessfulGrantAccess() {
        var assessment = AssessmentMother.assessment();
        var param = createParam(b -> b.assessmentId(assessment.getId()));
        var user = new User(UUID.randomUUID(), "Display Name", "user@mail.com");

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(param.getEmail())).thenReturn(Optional.of(user));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        when(checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())).thenReturn(false);
        doNothing().when(grantUserAssessmentRolePort).persist(assessment.getId(), user.getId(), param.getRoleId());
        doNothing().when(createAssessmentSpaceUserAccessPort).persist(any(CreateAssessmentSpaceUserAccessPort.Param.class));

        service.inviteUser(param);

        ArgumentCaptor<CreateAssessmentSpaceUserAccessPort.Param> spaceAccessParamCaptor =
            ArgumentCaptor.forClass(CreateAssessmentSpaceUserAccessPort.Param.class);
        verify(createAssessmentSpaceUserAccessPort).persist(spaceAccessParamCaptor.capture());
        assertEquals(param.getAssessmentId(), spaceAccessParamCaptor.getValue().assessmentId());
        assertEquals(user.getId(), spaceAccessParamCaptor.getValue().userId());
        assertEquals(param.getCurrentUserId(), spaceAccessParamCaptor.getValue().createdBy());
        assertNotNull(spaceAccessParamCaptor.getValue().creationTime());

        verifyNoInteractions(sendEmailPort, createAssessmentInvitePort, createSpaceInvitePort);
    }

    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setEmail(new AppSpecProperties.Email());
        properties.getEmail().setFromDisplayName("Assessment Platform");
        properties.setHost("platform.org");
        properties.setName("AssessmentPlatform");
        properties.setSupportEmail("support@platform.org");
        return properties;
    }

    private InviteAssessmentUserUseCase.Param createParam(Consumer<InviteAssessmentUserUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InviteAssessmentUserUseCase.Param.ParamBuilder paramBuilder() {
        return InviteAssessmentUserUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .email("user@email.com")
            .roleId(3)
            .currentUserId(UUID.randomUUID());
    }
}
