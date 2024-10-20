package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.users.application.port.in.spaceuseraccess.AcceptSpaceInvitationsUseCase;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.users.test.fixture.application.SpaceInviteeMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptSpaceInvitationsServiceTest {

    @InjectMocks
    private AcceptSpaceInvitationsService service;
    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    @Mock
    private LoadSpaceUserInvitationsPort loadSpaceUserInvitationsPort;
    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;
    @Mock
    private DeleteSpaceUserInvitationsPort deleteSpaceUserInvitationsPort;

    @Test
    @DisplayName("If user is not invited to any spaces, it should not throws any exceptions")
    void testAcceptSpaceInvitations_invitedSpacesIsNull_successful() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadSpaceUserInvitationsPort.loadInvitations(email)).thenReturn(List.of());

        assertDoesNotThrow(() -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadSpaceUserInvitationsPort).loadInvitations(email);
        verifyNoInteractions(deleteSpaceUserInvitationsPort);
        verifyNoInteractions(createSpaceUserAccessPort);
    }

    @Test
    @DisplayName("If user is invited to some spaces, it should not throws any exceptions")
    void testAcceptSpaceInvitations_invitedSpacesIsNotEmpty_successful() {
        UUID userId = UUID.randomUUID();
        long spaceId = 0L;
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId);
        var portResult = Stream.of(SpaceInviteeMother.notExpiredInvitee(spaceId, email)).toList();

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadSpaceUserInvitationsPort.loadInvitations(email)).thenReturn(portResult);
        doNothing().when(createSpaceUserAccessPort).persistAll(any());

        assertDoesNotThrow(() -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadSpaceUserInvitationsPort).loadInvitations(email);
        verify(createSpaceUserAccessPort).persistAll(any());
        verify(deleteSpaceUserInvitationsPort).deleteAll(email);
    }

    @Test
    @DisplayName("If user is invited to some spaces, but invitation is expired, it should not throws any exceptions")
    void testAcceptSpaceInvitations_invitedSpacesIsExpired_successful() {
        UUID userId = UUID.randomUUID();
        long spaceId = 0L;
        String email = "test@example.com";
        AcceptSpaceInvitationsUseCase.Param param = new AcceptSpaceInvitationsUseCase.Param(userId);
        var portResult = Stream.of(SpaceInviteeMother.expiredInvitee(spaceId, email)).toList();

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadSpaceUserInvitationsPort.loadInvitations(email)).thenReturn(portResult);
        doNothing().when(deleteSpaceUserInvitationsPort).deleteAll(email);

        assertDoesNotThrow(() -> service.acceptInvitations(param));
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadSpaceUserInvitationsPort).loadInvitations(email);
        verifyNoInteractions(createSpaceUserAccessPort);
        verify(deleteSpaceUserInvitationsPort).deleteAll(email);
    }
}
