package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.LoadUserInvitedSpacesPort;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptSpaceInvitationsServiceTest {

    @InjectMocks
    private AcceptSpaceInvitationsService service;
    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    @Mock
    private LoadUserInvitedSpacesPort loadUserInvitedSpacesPort;
    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;
    //@Mock
    //private DeleteUserInvitation deleteUserInvitation;

    @Test
    @DisplayName("UserId and Email should be matched")
    void testAcceptSpaceInvitations_userIdAndEmailNotFound_NotFoundException() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId, email);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn("another@example.ir");

        assertThrows(ResourceNotFoundException.class, () -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
    }

    @Test
    @DisplayName("Email of provided 'UserId' should be present and shouldn't be null")
    void testAcceptSpaceInvitations_emailIsNull_NotFoundException() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId, email);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verifyNoInteractions(loadUserInvitedSpacesPort);
    }

    @Test
    @DisplayName("If user does not invited to any spaces, it should not throws any exceptions")
    void testAcceptSpaceInvitations_invitedSpacesIsNull_successful() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId, email);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadUserInvitedSpacesPort.loadSpacesIds(email)).thenReturn(null);

        assertDoesNotThrow(() -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadUserInvitedSpacesPort).loadSpacesIds(email);
    }

    @Test
    @DisplayName("If user invited to some spaces, it should not throws any exceptions")
    void testAcceptSpaceInvitations_invitedSpacesIsNotEmpty_successful() {
        UUID userId = UUID.randomUUID();
        long spaceId = 0L;
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId, email);
        var portResult = Stream.of(new LoadUserInvitedSpacesPort.Result(spaceId, userId)).toList();

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadUserInvitedSpacesPort.loadSpacesIds(email)).thenReturn(portResult);
        doNothing().when(createSpaceUserAccessPort).createAccess(any());

        assertDoesNotThrow(() -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadUserInvitedSpacesPort).loadSpacesIds(email);
        verify(createSpaceUserAccessPort).createAccess(any());
    }
}
