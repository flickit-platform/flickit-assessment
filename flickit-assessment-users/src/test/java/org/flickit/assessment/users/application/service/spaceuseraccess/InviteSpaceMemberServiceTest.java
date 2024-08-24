package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.application.port.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE;
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
    CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Mock
    InviteSpaceMemberPort inviteSpaceMemberPort;

    @Mock
    AppSpecProperties appSpecProperties;

    @Mock
    SendEmailPort sendEmailPort;

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
        verifyNoInteractions(inviteSpaceMemberPort);
        verifyNoInteractions(sendEmailPort);
    }

    @Test
    @DisplayName("Inviting space member to a space should cause ResourceAlreadyExistException")
    void inviteMember_inviteeIsMember_ResourceAlreadyExistException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID inviteeUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(Optional.of(inviteeUserId));
        when(checkSpaceAccessPort.checkIsMember(spaceId, inviteeUserId)).thenReturn(true);
        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteMember(param));
        assertEquals(INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
        verifyNoInteractions(inviteSpaceMemberPort);
        verifyNoInteractions(sendEmailPort);
    }

    @Test
    @DisplayName("Inviting non-member flickit user to a space should cause successful insertion in SpaceUserAccess")
    void inviteMember_inviteeIsFlickitUser_AddAsSpaceMember() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID inviteeUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(Optional.of(inviteeUserId));
        when(checkSpaceAccessPort.checkIsMember(spaceId, inviteeUserId)).thenReturn(false);
        doNothing().when(createSpaceUserAccessPort).persist(isA(SpaceUserAccess.class));
        assertDoesNotThrow(() -> service.inviteMember(param));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
        verifyNoInteractions(inviteSpaceMemberPort);
        verifyNoInteractions(sendEmailPort);
    }

    @Test
    @DisplayName("Inviting non-user to a valid space should cause successful insertion")
    void inviteMember_validParameters_successful() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var usecaseParam = new InviteSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(Optional.empty());
        doNothing().when(inviteSpaceMemberPort).invite(isA(InviteSpaceMemberPort.Param.class));
        doNothing().when(sendEmailPort).send(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> service.inviteMember(usecaseParam));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
        verify(inviteSpaceMemberPort).invite(any(InviteSpaceMemberPort.Param.class));
        verify(appSpecProperties, times(2)).getName();
        verify(appSpecProperties).getHost();
    }
}
