package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteTokenValidationPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmExpertGroupInvitationServiceTest {

    @InjectMocks
    private ConfirmExpertGroupInvitationService service;
    @Mock
    private CheckInviteTokenValidationPort checkInviteTokenValidationPort;
    @Mock
    private ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    @Test
    void testInviteMember_validParameters_success() {
        UUID userId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        doNothing().when(checkInviteTokenValidationPort).checkToken(isA(Long.class), isA(UUID.class), isA(UUID.class));
        doNothing().when(confirmExpertGroupInvitationPort).confirmInvitation(isA(UUID.class));

        Assertions.assertDoesNotThrow(() -> service.confirmInvitation(expertGroupId, userId, inviteToken));

        verify(checkInviteTokenValidationPort).checkToken(anyLong(), any(UUID.class), any(UUID.class));
        verify(confirmExpertGroupInvitationPort).confirmInvitation(any(UUID.class));
    }
}
