package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteInputDataValidityPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteTokenExpiryPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmExpertGroupInvitationServiceTest {

    @InjectMocks
    private ConfirmExpertGroupInvitationService service;
    @Mock
    private CheckInviteInputDataValidityPort checkInviteInputDataValidityPort;
    @Mock
    private CheckInviteTokenExpiryPort checkInviteTokenExpiryPort;
    @Mock
    private ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    @Test
    @DisplayName("Confirm invite member invitation with valid parameters causes success confirmation")
    void testInviteMemberConfirmation_validParameters_success() {
        UUID userId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        when(checkInviteInputDataValidityPort.checkInputData(expertGroupId,userId, inviteToken)).thenReturn(true);
        when(checkInviteTokenExpiryPort.isInviteTokenValid(inviteToken)).thenReturn(true);
        doNothing().when(confirmExpertGroupInvitationPort).confirmInvitation(isA(UUID.class));

        Assertions.assertDoesNotThrow(() -> service.confirmInvitation(expertGroupId, userId, inviteToken));

        verify(checkInviteInputDataValidityPort).checkInputData(anyLong(), any(UUID.class), any(UUID.class));
        verify(confirmExpertGroupInvitationPort).confirmInvitation(any(UUID.class));
    }

    @Test
    @DisplayName("Confirm invite member invitation with invalid parameters causes ResourceNotFoundException")
    void testConfirmInviteMember_invalidInputData_fail() {
        UUID userId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        when(checkInviteInputDataValidityPort.checkInputData(anyLong(),any(UUID.class), any(UUID.class))).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class,
            () -> service.confirmInvitation(expertGroupId, userId, inviteToken));

        verify(checkInviteInputDataValidityPort).checkInputData(anyLong(), any(UUID.class), any(UUID.class));
        verifyNoInteractions(checkInviteTokenExpiryPort);
        verifyNoInteractions(confirmExpertGroupInvitationPort);
    }

    @Test
    @DisplayName("Confirm invite member invitation with invalid parameters causes ResourceNotFoundException")
    void testConfirmInviteMember_expiredInviteToken_fail() {
        UUID userId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        when(checkInviteInputDataValidityPort.checkInputData(anyLong(),any(UUID.class), any(UUID.class))).thenReturn(true);
        when(checkInviteTokenExpiryPort.isInviteTokenValid(inviteToken)).thenReturn(false);


        Assertions.assertThrows(ResourceNotFoundException.class,
            () -> service.confirmInvitation(expertGroupId, userId, inviteToken));

        verify(checkInviteInputDataValidityPort).checkInputData(anyLong(), any(UUID.class), any(UUID.class));
        verify(checkInviteTokenExpiryPort).isInviteTokenValid(any(UUID.class));
        verifyNoInteractions(confirmExpertGroupInvitationPort);
    }
}
