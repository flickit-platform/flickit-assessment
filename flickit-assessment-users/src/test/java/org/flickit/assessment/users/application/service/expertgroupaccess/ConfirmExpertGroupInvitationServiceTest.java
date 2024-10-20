package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.ConfirmExpertGroupInvitationUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmExpertGroupInvitationServiceTest {

    @InjectMocks
    private ConfirmExpertGroupInvitationService service;
    @Mock
    private LoadExpertGroupAccessPort loadExpertGroupAccessPort;
    @Mock
    private ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    @Test
    @DisplayName("Confirm invite member invitation with valid parameters causes success confirmation")
    void testInviteMemberConfirmation_validParameters_success() {
        UUID currentUserId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        Param param = new Param(expertGroupId, inviteToken, currentUserId);
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);
        ExpertGroupAccess expertGroupAccess =
            new ExpertGroupAccess(expirationDate, inviteToken, ExpertGroupAccessStatus.PENDING.ordinal());

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroupId, currentUserId))
            .thenReturn(Optional.of(expertGroupAccess));
        doNothing().when(confirmExpertGroupInvitationPort).confirmInvitation(expertGroupId, currentUserId);

        assertDoesNotThrow(() -> service.confirmInvitation(param));

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(anyLong(), any(UUID.class));
        verify(confirmExpertGroupInvitationPort).confirmInvitation(anyLong(), any(UUID.class));
    }

    @Test
    @DisplayName("Confirm invite member invitation with invalid parameters causes ResourceNotFoundException")
    void testConfirmInviteMember_invalidInputData_resourceNotFound() {
        UUID currentUserId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        Param param = new Param(expertGroupId, inviteToken, currentUserId);

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroupId, currentUserId))
            .thenThrow(new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_LINK_INVALID));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.confirmInvitation(param));
        Assertions.assertThat(throwable).hasMessage(CONFIRM_EXPERT_GROUP_INVITATION_LINK_INVALID);

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(anyLong(), any(UUID.class));
        verifyNoInteractions(confirmExpertGroupInvitationPort);
    }

    @Test
    @DisplayName("Confirm invite member invitation with expired invite token causes validationException")
    void testConfirmInviteMember_expiredInviteToken_validationException() {
        UUID currentUserId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        Param param = new Param(expertGroupId, inviteToken, currentUserId);
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(1);
        var expertGroupAccess = new ExpertGroupAccess(expirationDate, inviteToken, ExpertGroupAccessStatus.PENDING.ordinal());

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroupId, currentUserId))
            .thenReturn(Optional.of(expertGroupAccess));

        var throwable = assertThrows(ValidationException.class, () -> service.confirmInvitation(param));
        assertEquals(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED, throwable.getMessageKey());

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(anyLong(), any(UUID.class));
        verifyNoInteractions(confirmExpertGroupInvitationPort);
    }

    @Test
    @DisplayName("Confirm invite member invitation with invalid invite token causes validationException")
    void testConfirmInviteMember_invalidInviteToken_validationException() {
        UUID currentUserId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        Param param = new Param(expertGroupId, inviteToken, currentUserId);
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(1);
        var expertGroupAccess = new ExpertGroupAccess(expirationDate, UUID.randomUUID(), ExpertGroupAccessStatus.PENDING.ordinal());

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroupId, currentUserId))
            .thenReturn(Optional.of(expertGroupAccess));

        var throwable = assertThrows(ValidationException.class, () -> service.confirmInvitation(param));
        assertEquals(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_INVALID, throwable.getMessageKey());

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(anyLong(), any(UUID.class));
        verifyNoInteractions(confirmExpertGroupInvitationPort);
    }

    @Test
    @DisplayName("Confirm invite member invitation with an for an active member causes ResourceAlreadyExistsException")
    void testConfirmInviteMember_activeStatus_alreadyExist() {
        UUID currentUserId = UUID.randomUUID();
        UUID inviteToken = UUID.randomUUID();
        long expertGroupId = 0L;

        Param param = new Param(expertGroupId, inviteToken, currentUserId);
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        var expertGroupAccess = new ExpertGroupAccess(expirationDate, inviteToken, ExpertGroupAccessStatus.ACTIVE.ordinal());

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroupId, currentUserId))
            .thenReturn(Optional.of(expertGroupAccess));

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.confirmInvitation(param));
        Assertions.assertThat(throwable).hasMessage(CONFIRM_EXPERT_GROUP_INVITATION_USER_ID_DUPLICATE);

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(anyLong(), any(UUID.class));
        verifyNoInteractions(confirmExpertGroupInvitationPort);
    }
}
