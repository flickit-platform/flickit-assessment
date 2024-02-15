package org.flickit.assessment.kit.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.*;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteTokenCheckPort;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserEmailByUserIdPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteExpertGroupMemberServiceTest {


    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    @Mock
    private CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    @Mock
    private CheckExpertGroupOwnerPort checkExpertGroupOwnerPort;
    @Mock
    private InviteTokenCheckPort inviteTokenCheckPort;

    @Mock
    private InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    @Mock
    private SendExpertGroupInvitationMailPort sendExpertGroupInvitationMailPort;
    @InjectMocks
    private InviteExpertGroupMemberService service;

    UUID userId = UUID.randomUUID();
    long expertGroupId = 0L;
    UUID currentUserId = UUID.randomUUID();
    InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
    String email = "test@example.com";

    @Test
    void inviteMember_validParameters_success() {

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(true);
        when(inviteTokenCheckPort.checkInviteToken(any(UUID.class))).thenReturn(true);
        doNothing().when(inviteExpertGroupMemberPort).persist(isA(InviteExpertGroupMemberPort.Param.class));
        doNothing().when(sendExpertGroupInvitationMailPort).sendInviteExpertGroupMemberEmail(isA(String.class), isA(UUID.class));

        service.inviteMember(param);

        verify(loadUserEmailByUserIdPort).loadEmail(any(UUID.class));
        verify(checkExpertGroupExistsPort).existsById(any(Long.class));
        verify(checkExpertGroupOwnerPort).checkIsOwner(any(Long.class), any(UUID.class));
        verify(inviteExpertGroupMemberPort).persist(any());
        verify(inviteTokenCheckPort).checkInviteToken(any(UUID.class));
        verify(sendExpertGroupInvitationMailPort).sendInviteExpertGroupMemberEmail(any(String.class), any(UUID.class));
    }

    @Test
    void inviteMember_expertGroupNotExist_fail() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        String email = "test@example.com";

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.inviteMember(param));

    }

    @Test
    void inviteMember_expertGroupInviterNotOwner_fail() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        InviteExpertGroupMemberUseCase.Param param = new InviteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        String email = "test@example.com";

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(false);

        Assertions.assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));

    }
}
