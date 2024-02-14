package org.flickit.assessment.kit.application.service.expertgroupaccess;

import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteTokenCheckPort;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserEmailByUserIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteExpertGroupMemberServiceTest {

    @Mock
    private InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    @Mock
    private SendExpertGroupInvitationMailPort sendExpertGroupInvitationMailPort;
    @Mock
    private InviteTokenCheckPort inviteTokenCheckPort;

    @InjectMocks
    private InviteExpertGroupMemberService service;

    @Test
    void inviteMember_Success() {
        Param param = new Param(new Random().nextLong(), UUID.randomUUID(), UUID.randomUUID());
        String email = "test@example.com";

        when(loadUserEmailByUserIdPort.loadEmail(any())).thenReturn(email);
        when(inviteTokenCheckPort.checkInviteToken(any(UUID.class))).thenReturn(true);

        service.inviteMember(param);

        verify(inviteExpertGroupMemberPort).persist(any());
        verify(sendExpertGroupInvitationMailPort).sendInviteExpertGroupMemberEmail(any(String.class), any(UUID.class));
        verify(inviteTokenCheckPort).checkInviteToken(any(UUID.class));
    }
}
