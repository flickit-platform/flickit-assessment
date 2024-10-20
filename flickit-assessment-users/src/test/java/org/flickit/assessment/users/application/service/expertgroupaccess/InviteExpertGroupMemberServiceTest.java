package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMemberStatusPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus.ACTIVE;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE;
import static org.flickit.assessment.users.common.MessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteExpertGroupMemberServiceTest {

    @InjectMocks
    private InviteExpertGroupMemberService service;

    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;

    @Mock
    private InviteExpertGroupMemberPort inviteExpertGroupMemberPort;

    @Mock
    private SendEmailPort sendEmailPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadExpertGroupMemberStatusPort loadExpertGroupMemberPort;

    @Spy // @Spy added for injecting this field in #service
    AppSpecProperties appSpecProperties = appSpecProperties();

    @Test
    void testInviteMember_validParameters_success() {
        var param = createParam(InviteExpertGroupMemberUseCase.Param.ParamBuilder::build);
        String email = "test@example.com";
        UUID mockInviteToken = UUID.randomUUID();

        when(loadUserEmailByUserIdPort.loadEmail(param.getUserId())).thenReturn(email);
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadExpertGroupMemberPort.getMemberStatus(param.getExpertGroupId(), param.getUserId())).thenReturn(Optional.empty());
        doNothing().when(inviteExpertGroupMemberPort).invite(any());
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        try (var mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(mockInviteToken);

            service.inviteMember(param);

            ArgumentCaptor<InviteExpertGroupMemberPort.Param> invitePortParamCaptor = ArgumentCaptor.forClass(InviteExpertGroupMemberPort.Param.class);
            verify(inviteExpertGroupMemberPort).invite(invitePortParamCaptor.capture());
            assertEquals(param.getExpertGroupId(), invitePortParamCaptor.getValue().expertGroupId());
            assertNotNull(invitePortParamCaptor.getValue().inviteDate());
            assertEquals(param.getUserId(), invitePortParamCaptor.getValue().userId());
            assertEquals(invitePortParamCaptor.getValue().inviteDate().plusDays(7), invitePortParamCaptor.getValue().inviteExpirationDate());
            assertNotNull(invitePortParamCaptor.getValue().inviteToken());
            assertEquals(ExpertGroupAccessStatus.PENDING, invitePortParamCaptor.getValue().status());
            assertEquals(param.getCurrentUserId(), invitePortParamCaptor.getValue().createdBy());

            String subject = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT, appSpecProperties.getName());
            String inviteUrl = String.join("/", appSpecProperties.getHost(), appSpecProperties.getExpertGroupInviteUrlPath(),
                String.valueOf(param.getExpertGroupId()), mockInviteToken.toString());
            String body = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY,
                inviteUrl,
                appSpecProperties.getName(),
                appSpecProperties.getSupportEmail());
            verify(sendEmailPort).send(email, subject, body);
        }
    }

    @Test
    void testInviteMember_validParameters_sendInvitationWithoutSupportEmail() {
        var param = createParam(InviteExpertGroupMemberUseCase.Param.ParamBuilder::build);
        String email = "test@example.com";
        UUID mockInviteToken = UUID.randomUUID();

        when(loadUserEmailByUserIdPort.loadEmail(param.getUserId())).thenReturn(email);
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadExpertGroupMemberPort.getMemberStatus(param.getExpertGroupId(), param.getUserId())).thenReturn(Optional.empty());
        doNothing().when(inviteExpertGroupMemberPort).invite(any());
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        try (var mockedUUID = mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(mockInviteToken);

            appSpecProperties.setSupportEmail("  ");

            service.inviteMember(param);

            String subject = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT, appSpecProperties.getName());
            String inviteUrl = String.join("/", appSpecProperties.getHost(), appSpecProperties.getExpertGroupInviteUrlPath(),
                String.valueOf(param.getExpertGroupId()), mockInviteToken.toString());
            String body = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY_WITHOUT_SUPPORT_EMAIL,
                inviteUrl,
                appSpecProperties.getName());
            verify(sendEmailPort).send(email, subject, body);
        }
    }

    @Test
    void testInviteMember_memberExistAndActive_alreadyExist() {
        var param = createParam(InviteExpertGroupMemberUseCase.Param.ParamBuilder::build);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadExpertGroupMemberPort.getMemberStatus(param.getExpertGroupId(), param.getUserId())).thenReturn(Optional.of(ACTIVE.ordinal()));

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));
        assertThat(throwable).hasMessage(INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE);

        verifyNoInteractions(inviteExpertGroupMemberPort, sendEmailPort, loadUserEmailByUserIdPort);
    }

    @Test
    void testInviteMember_expertGroupNotExist_throwResourceNotFoundException() {
        var param = createParam(InviteExpertGroupMemberUseCase.Param.ParamBuilder::build);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.inviteMember(param));
        assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);

        verifyNoInteractions(inviteExpertGroupMemberPort, sendEmailPort, loadUserEmailByUserIdPort,
            loadExpertGroupMemberPort);
    }

    @Test
    void testInviteMember_expertGroupInviterNotOwner_throwAccessDeniedException() {
        var param = createParam(InviteExpertGroupMemberUseCase.Param.ParamBuilder::build);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(inviteExpertGroupMemberPort, sendEmailPort, loadUserEmailByUserIdPort,
            loadExpertGroupMemberPort);
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

    private InviteExpertGroupMemberUseCase.Param createParam(Consumer<InviteExpertGroupMemberUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InviteExpertGroupMemberUseCase.Param.ParamBuilder paramBuilder() {
        return InviteExpertGroupMemberUseCase.Param.builder()
            .expertGroupId(123L)
            .userId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
