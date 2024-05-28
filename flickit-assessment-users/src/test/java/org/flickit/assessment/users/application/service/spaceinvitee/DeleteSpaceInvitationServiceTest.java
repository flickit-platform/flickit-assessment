package org.flickit.assessment.users.application.service.spaceinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.domain.SpaceInvitee;
import org.flickit.assessment.users.application.port.in.spaceinvitee.DeleteSpaceInvitationUseCase.Param;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceInvitationPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInvitationPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceInvitationServiceTest {

    @InjectMocks
    DeleteSpaceInvitationService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    LoadSpaceInvitationPort loadSpaceInvitationPort;

    @Mock
    DeleteSpaceInvitationPort deleteSpaceInvitationPort;

    @Test
    @DisplayName("Deleting a space's invitation, should be done by owner")
    void testDeleteSpaceInvitation_invalidOwner_userNotAllowed() {
        UUID inviteId = UUID.randomUUID();
        String email = "admin@flickit.ir";
        long spaceId = 0L;
        var currentUserId = UUID.randomUUID();
        Param param = new Param(inviteId, currentUserId);
        SpaceInvitee spaceInvitee = new SpaceInvitee(inviteId, email, spaceId, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.MAX);
        when(loadSpaceInvitationPort.loadSpaceInvitation(inviteId)).thenReturn(spaceInvitee);
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        Throwable throwable = assertThrows(AccessDeniedException.class,
            () -> service.deleteInvitation(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(loadSpaceInvitationPort).loadSpaceInvitation(inviteId);
        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(deleteSpaceInvitationPort);
    }

    @Test
    @DisplayName("Deleting a space's invitation, with valid parameters should be successful")
    void testDeleteSpaceInvitation_validParameters_successful() {
        UUID inviteId = UUID.randomUUID();
        String email = "admin@flickit.ir";
        long spaceId = 0L;
        var currentUserId = UUID.randomUUID();
        Param param = new Param(inviteId, currentUserId);
        SpaceInvitee spaceInvitee = new SpaceInvitee(inviteId, email, spaceId, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.MAX);
        when(loadSpaceInvitationPort.loadSpaceInvitation(inviteId)).thenReturn(spaceInvitee);
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteInvitation(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        verify(loadSpaceInvitationPort).loadSpaceInvitation(inviteId);
        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(deleteSpaceInvitationPort).deleteSpaceInvitation(inviteId);
    }
}
