package org.flickit.assessment.users.application.service.spaceaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceaccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceaccess.CheckMemberSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceaccess.CheckSpaceExistencePort;
import org.flickit.assessment.users.application.port.out.spaceaccess.InviteSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private CheckMemberSpaceAccessPort checkMemberSpaceAccessPort;
    @Mock
    LoadUserIdByEmailPort loadUserIdByEmailPort;
    @Mock
    InviteSpaceMemberPort inviteSpaceMemberPort;

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
        verifyNoInteractions(checkMemberSpaceAccessPort);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(inviteSpaceMemberPort);
    }

    @Test
    @DisplayName("Inviting member to a space by a non-member should cause AccessDeniedException")
    void inviteMember_inviterIsNotMember_AccessDeniedException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(true);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkMemberSpaceAccessPort).checkAccess(currentUserId);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(inviteSpaceMemberPort);
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
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(userId);

        assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkMemberSpaceAccessPort).checkAccess(currentUserId);
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verifyNoInteractions(inviteSpaceMemberPort);

    }

    @Test
    @DisplayName("Inviting non-member to a valid space should cause successful insertion")
    void inviteMember_validParameters_successful() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var usecaseParam = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(true);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(null);
        doNothing().when(inviteSpaceMemberPort).inviteMember(isA(InviteSpaceMemberPort.Param.class));

        assertDoesNotThrow(() -> service.inviteMember(usecaseParam));

        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkSpaceExistencePort).existsById(spaceId);
        verify(checkMemberSpaceAccessPort).checkAccess(currentUserId);
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verify(inviteSpaceMemberPort).inviteMember(any(InviteSpaceMemberPort.Param.class));
    }
}
