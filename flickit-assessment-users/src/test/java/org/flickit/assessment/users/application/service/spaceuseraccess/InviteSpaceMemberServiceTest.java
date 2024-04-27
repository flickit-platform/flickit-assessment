package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.port.in.spaceaccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SaveSpaceMemberInviteePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SendInviteMailPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
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
class InviteSpaceMemberServiceTest {

    @InjectMocks
    InviteSpaceMemberService service;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    SaveSpaceMemberInviteePort saveSpaceMemberInviteePort;

    @Mock
    SendInviteMailPort SendInviteMailPort;

    @Test
    @DisplayName("Inviting member to a space by a non-member should cause AccessDeniedException")
    void inviteMember_inviterIsNotMember_AccessDeniedException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(loadUserPort);
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
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(userId);

        assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
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
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(null);
        doNothing().when(saveSpaceMemberInviteePort).persist(isA(SaveSpaceMemberInviteePort.Param.class));
        doNothing().when(SendInviteMailPort).sendInviteMail(email);

        assertDoesNotThrow(() -> service.inviteMember(usecaseParam));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
        verify(saveSpaceMemberInviteePort).persist(any(SaveSpaceMemberInviteePort.Param.class));
        verify(SendInviteMailPort).sendInviteMail(email);
    }
}
