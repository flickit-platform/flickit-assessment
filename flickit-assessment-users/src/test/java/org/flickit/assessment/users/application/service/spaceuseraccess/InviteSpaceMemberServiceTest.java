package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceaccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SendInviteMailPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceMemberAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceExistencePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SaveSpaceMemberInviteePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteSpaceMemberServiceTest {

    @InjectMocks
    InviteSpaceMemberService service;
    @Mock
    private CheckSpaceExistencePort checkSpaceExistencePort;
    @Mock
    private CheckSpaceMemberAccessPort checkSpaceMemberAccessPort;
    @Mock
    LoadUserIdByEmailPort loadUserIdByEmailPort;
    @Mock
    SaveSpaceMemberInviteePort saveSpaceMemberInviteePort;
    @Mock
    SendInviteMailPort SendInviteMailPort;

    @Test
    @DisplayName("Inviting member to an invalid space should cause a ValidationException")
    void inviteMember_spaceNotFound_validationException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(false);

        assertThrows(ValidationException.class, () -> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verifyNoInteractions(checkSpaceMemberAccessPort);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(saveSpaceMemberInviteePort);
        verifyNoInteractions(SendInviteMailPort);
    }

    @Test
    @DisplayName("Inviting member to a space by a non-member should cause AccessDeniedException")
    void inviteMember_inviterIsNotMember_AccessDeniedException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(true);
        when(checkSpaceMemberAccessPort.checkIsMember(currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkSpaceMemberAccessPort).checkIsMember(currentUserId);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(saveSpaceMemberInviteePort);
        verifyNoInteractions(SendInviteMailPort);
    }

    @Test
    @DisplayName("Inviting member already-member to a space should cause AlreadyExistException")
    void inviteMember_inviteeIsAlreadyMember_AlreadyExistException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(true);
        when(checkSpaceMemberAccessPort.checkIsMember(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(userId);

        assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkSpaceMemberAccessPort).checkIsMember(currentUserId);
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verifyNoInteractions(saveSpaceMemberInviteePort);
        verifyNoInteractions(SendInviteMailPort);
    }

    @Test
    @DisplayName("Inviting non-member to a valid space should cause successful insertion")
    void inviteMember_validParameters_successful() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var usecaseParam = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(true);
        when(checkSpaceMemberAccessPort.checkIsMember(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(null);
        doNothing().when(saveSpaceMemberInviteePort).persist(isA(SaveSpaceMemberInviteePort.Param.class));
        doNothing().when(SendInviteMailPort).sendInviteMail(email);

        assertDoesNotThrow(() -> service.inviteMember(usecaseParam));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkSpaceMemberAccessPort).checkIsMember(currentUserId);
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verify(saveSpaceMemberInviteePort).persist(any(SaveSpaceMemberInviteePort.Param.class));
        verify(SendInviteMailPort).sendInviteMail(email);
    }
}
