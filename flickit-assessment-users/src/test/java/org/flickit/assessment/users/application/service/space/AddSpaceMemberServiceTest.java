package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.AddSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.service.spaceuseraccess.AddSpaceMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_BY_EMAIL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddSpaceMemberServiceTest {

    @InjectMocks
    AddSpaceMemberService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private AddSpaceMemberPort addSpaceMemberPort;

    @Test
    @DisplayName("Adding a valid member to a valid space should cause a successful addition")
    void addSpaceMember_validParameters_successful() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(userId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, userId)).thenReturn(false);
        doNothing().when(addSpaceMemberPort).persist(isA(AddSpaceMemberPort.Param.class));

        assertDoesNotThrow(() -> service.addMember(param));

        verify(checkSpaceAccessPort, times(2)).checkIsMember(anyLong(), any(UUID.class));
        verify(loadUserPort).loadUserIdByEmail(email);
        verify(addSpaceMemberPort).persist(any(AddSpaceMemberPort.Param.class));
    }

    @Test
    @DisplayName("Adding a member to a valid space should be done by a member; otherwise causes AccessDeniedException")
    void addSpaceMember_inviterIsNotSpaceMember_AccessDeniedExceptionException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(loadUserPort);
        verifyNoInteractions(addSpaceMemberPort);
    }

    @Test
    @DisplayName("Adding a non-flickit user to a space should cause ResourceNotFoundException")
    void addSpaceMember_inviteeIsNotFlickitUser_ResourceNotFoundException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenThrow(new ResourceNotFoundException(USER_BY_EMAIL_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.addMember(param));
        assertEquals(USER_BY_EMAIL_NOT_FOUND, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
        verifyNoInteractions(addSpaceMemberPort);
    }

    @Test
    @DisplayName("Adding an already-member user to a space should cause ResourceAlreadyExistsException")
    void addSpaceMember_inviteeIsMember_ResourceAlreadyExistsException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(userId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, userId)).thenReturn(true);

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.addMember(param));
        assertEquals(ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE, throwable.getMessage());

        verify(checkSpaceAccessPort, times(2)).checkIsMember(anyLong(), any(UUID.class));
        verify(loadUserPort).loadUserIdByEmail(email);
        verifyNoInteractions(addSpaceMemberPort);
    }
}
