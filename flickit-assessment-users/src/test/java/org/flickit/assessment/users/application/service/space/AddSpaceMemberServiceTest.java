package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.out.space.CheckMemberSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.space.AddSpaceMemberPort;
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
class AddSpaceMemberServiceTest {

    @InjectMocks
    AddSpaceMemberService service;
    @Mock
    private LoadSpacePort loadSpacePort;
    @Mock
    private LoadUserIdByEmailPort loadUserIdByEmailPort;
    @Mock
    private CheckMemberSpaceAccessPort checkMemberSpaceAccessPort;
    @Mock
    private AddSpaceMemberPort addSpaceMemberPort;

    @Test
    @DisplayName("Adding a valid member to a valid space should cause a successful addition")
    void addSpaceMember_validParameters_successful() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var portResult = new LoadSpacePort.Result("spaceTitle");

        when(loadSpacePort.loadById(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(userId);
        when(checkMemberSpaceAccessPort.checkAccess(userId)).thenReturn(false);
        doNothing().when(addSpaceMemberPort).addMemberAccess(isA(Long.class),
            isA(UUID.class), isA(UUID.class), isA(LocalDateTime.class));

        assertDoesNotThrow(() -> service.addMember(spaceId, email, currentUserId));

        verify(loadSpacePort).loadById(spaceId);
        verify(checkMemberSpaceAccessPort, times(2)).checkAccess(any(UUID.class));
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verify(addSpaceMemberPort).addMemberAccess(anyLong(), any(UUID.class), any(UUID.class), any(LocalDateTime.class));

    }

    @Test
    @DisplayName("Adding a member to an invalid space should cause ResourceNotFoundException")
    void addSpaceMember_invalidSpace_ResourceNotFoundException(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();

        when(loadSpacePort.loadById(spaceId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, ()-> service.addMember(spaceId,email,currentUserId));
        verify(loadSpacePort).loadById(spaceId);
        verifyNoInteractions(checkMemberSpaceAccessPort);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(addSpaceMemberPort);
    }

    @Test
    @DisplayName("Adding a member to an invalid space should cause ResourceNotFoundException")
    void addSpaceMember_inviterIsNotSpaceMember_ValidationException(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var portResult = new LoadSpacePort.Result("spaceTitle");

        when(loadSpacePort.loadById(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(false);

        assertThrows(ValidationException.class, ()-> service.addMember(spaceId,email,currentUserId));
        verify(loadSpacePort).loadById(spaceId);
        verify(checkMemberSpaceAccessPort).checkAccess(currentUserId);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(addSpaceMemberPort);
    }
}
