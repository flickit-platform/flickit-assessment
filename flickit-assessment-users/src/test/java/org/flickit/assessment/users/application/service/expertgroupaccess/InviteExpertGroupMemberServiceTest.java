package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMemberStatusPort;
import org.flickit.assessment.users.application.port.out.mail.SendExpertGroupInviteMailPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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
    private SendExpertGroupInviteMailPort sendExpertGroupInviteMailPort;
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
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadExpertGroupMemberPort.getMemberStatus(expertGroupId, userId)).thenReturn(Optional.empty());
        doNothing().when(inviteExpertGroupMemberPort).invite(isA(InviteExpertGroupMemberPort.Param.class));
        doNothing().when(sendExpertGroupInviteMailPort).sendInvite(anyString(), anyLong(), any(UUID.class));

        service.inviteMember(param);

        verify(loadUserEmailByUserIdPort).loadEmail(any(UUID.class));
        verify(loadExpertGroupOwnerPort).loadOwnerId(any(Long.class));
        verify(inviteExpertGroupMemberPort).invite(any());
    }

    @Test
    void testInviteMember_memberExistAndActive_alreadyExist() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadExpertGroupMemberPort.getMemberStatus(expertGroupId, userId)).thenReturn(Optional.of(ExpertGroupAccessStatus.ACTIVE.ordinal()));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));

    }

    @Test
    void testInviteMember_expertGroupNotExist_fail() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.inviteMember(param));
    }

    @Test
    void testInviteMember_expertGroupInviterNotOwner_fail() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(UUID.randomUUID()));

        Assertions.assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));
    }
}
