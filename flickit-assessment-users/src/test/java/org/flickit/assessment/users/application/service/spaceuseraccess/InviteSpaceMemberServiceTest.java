package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
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

import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteSpaceMemberServiceTest {

    @InjectMocks
    InviteSpaceMemberService service;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Mock
    InviteSpaceMemberPort inviteSpaceMemberPort;

    @Mock
    SendEmailPort sendEmailPort;

    @Spy // @Spy added for injecting this field in #service
    AppSpecProperties appSpecProperties = appSpecProperties();

    @Test
    void testInviteMember_inviterIsNotMember_AccessDeniedException() {
        var param = createParam(InviteSpaceMemberUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadUserPort, inviteSpaceMemberPort, sendEmailPort);
    }

    @Test
    void testInviteMember_inviteeIsMember_ResourceAlreadyExistException() {
        UUID inviteeUserId = UUID.randomUUID();
        var param = createParam(InviteSpaceMemberUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.of(inviteeUserId));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), inviteeUserId)).thenReturn(true);

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));
        assertEquals(INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE, throwable.getMessage());

        verifyNoInteractions(inviteSpaceMemberPort, sendEmailPort);
    }

    @Test
    void testnviteMember_inviteeIsAnApplicationUser_AddAsSpaceMember() {
        UUID inviteeUserId = UUID.randomUUID();
        var param = createParam(InviteSpaceMemberUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.of(inviteeUserId));
        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), inviteeUserId)).thenReturn(false);
        doNothing().when(createSpaceUserAccessPort).persist(any());

        service.inviteMember(param);

        ArgumentCaptor<SpaceUserAccess> spaceUserAccessCaptor = ArgumentCaptor.forClass(SpaceUserAccess.class);
        verify(createSpaceUserAccessPort).persist(spaceUserAccessCaptor.capture());
        assertEquals(param.getSpaceId(), spaceUserAccessCaptor.getValue().getSpaceId());
        assertEquals(inviteeUserId, spaceUserAccessCaptor.getValue().getUserId());
        assertEquals(param.getCurrentUserId(), spaceUserAccessCaptor.getValue().getCreatedBy());
        assertNotNull(spaceUserAccessCaptor.getValue().getCreationTime());

        verifyNoInteractions(inviteSpaceMemberPort, sendEmailPort);
    }

    @Test
    void testInviteMember_validParametersInviteeIsNotAnApplicationUser_CreateInvitation() {
        var param = createParam(InviteSpaceMemberUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.empty());
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());
        doNothing().when(inviteSpaceMemberPort).invite(any());

        service.inviteMember(param);

        ArgumentCaptor<InviteSpaceMemberPort.Param> invitePortParamCaptor = ArgumentCaptor.forClass(InviteSpaceMemberPort.Param.class);
        verify(inviteSpaceMemberPort).invite(invitePortParamCaptor.capture());
        assertEquals(param.getSpaceId(), invitePortParamCaptor.getValue().spaceId());
        assertEquals(param.getEmail(), invitePortParamCaptor.getValue().email());
        assertEquals(param.getCurrentUserId(), invitePortParamCaptor.getValue().createdBy());
        assertNotNull(invitePortParamCaptor.getValue().creationTime());
        assertEquals(invitePortParamCaptor.getValue().creationTime().plusDays(7), invitePortParamCaptor.getValue().expirationDate());

        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY,
            appSpecProperties.getHost(),
            appSpecProperties.getName(),
            appSpecProperties.getSupportEmail());
        verify(sendEmailPort).send(param.getEmail(), subject, body);
    }

    @Test
    void testInviteMember_validParametersInviteeIsNotAnApplicationUser_SendInvitationWithoutSupportEmail() {
        var param = createParam(InviteSpaceMemberUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(param.getEmail())).thenReturn(Optional.empty());
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());
        doNothing().when(inviteSpaceMemberPort).invite(any());

        appSpecProperties.setSupportEmail(" ");

        service.inviteMember(param);

        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL,
            appSpecProperties.getHost(),
            appSpecProperties.getName());
        verify(sendEmailPort).send(param.getEmail(), subject, body);
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

    private InviteSpaceMemberUseCase.Param createParam(Consumer<InviteSpaceMemberUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InviteSpaceMemberUseCase.Param.ParamBuilder paramBuilder() {
        return InviteSpaceMemberUseCase.Param.builder()
            .spaceId(123L)
            .email("user@email.com")
            .currentUserId(UUID.randomUUID());
    }
}
