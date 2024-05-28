package org.flickit.assessment.users.application.service.spaceinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.DeleteSpaceInvitationUseCase.Param;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceInvitationPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInvitationPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    LoadSpaceInvitationPort loadSpaceInvitee;

    @Mock
    LoadSpaceInvitationPort deleteSpaceInvitationPort;

    @Test
    @DisplayName("Deleting a space's invitation, should be done by owner")
    void testDeleteSpaceInvitation_invalidOwner_userNotAllowed() {
        long spaceId = 0L;
        String email = "admin@flickit.ir";
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        Throwable throwable = assertThrows(AccessDeniedException.class,
            () -> service.deleteInvitation(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verifyNoInteractions(deleteSpaceInvitationPort);
    }

    @Test
    @DisplayName("Deleting a space's invitation, with valid parameters should be successful")
    void testDeleteSpaceInvitation_validParameters_successful() {
        long spaceId = 0L;
        String email = "admin@flickit.ir";
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, email, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);

        assertDoesNotThrow(() -> service.deleteInvitation(param));
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verify(deleteSpaceInvitationPort).deleteSpaceInvitation(spaceId, email);
    }
}
