package org.flickit.assessment.users.application.service.expertgroupaccess;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private AppSpecProperties appSpecProperties;
    @Mock
    private SendEmailPort sendEmailPort;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private LoadExpertGroupMemberStatusPort loadExpertGroupMemberPort;

    @Test
    void testInviteMember_validParameters_success() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
        String email = "test@example.com";

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupMemberPort.getMemberStatus(expertGroupId, userId)).thenReturn(Optional.empty());
        doNothing().when(inviteExpertGroupMemberPort).invite(isA(InviteExpertGroupMemberPort.Param.class));
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        service.inviteMember(param);

        verify(loadUserEmailByUserIdPort).loadEmail(any(UUID.class));
        verify(loadExpertGroupOwnerPort).loadOwnerId(any(Long.class));
        verify(inviteExpertGroupMemberPort).invite(any());
        verify(appSpecProperties).getName();
        verify(appSpecProperties).getHost();
        verify(appSpecProperties).getExpertGroupInviteUrlPath();
    }

    @Test
    void testInviteMember_memberExistAndActive_alreadyExist() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupMemberPort.getMemberStatus(expertGroupId, userId)).thenReturn(Optional.of(ExpertGroupAccessStatus.ACTIVE.ordinal()));

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));
        assertThat(throwable).hasMessage(INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE);
    }

    @Test
    void testInviteMember_expertGroupNotExist_fail() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenThrow (new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.inviteMember(param));
        assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);
    }

    @Test
    void testInviteMember_expertGroupInviterNotOwner_fail() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
