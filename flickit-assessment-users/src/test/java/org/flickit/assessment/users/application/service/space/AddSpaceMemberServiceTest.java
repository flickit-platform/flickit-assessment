package org.flickit.assessment.users.application.service.space;

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

        when(loadSpacePort.getById(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(userId);
        when(checkMemberSpaceAccessPort.checkAccess(userId)).thenReturn(false);
        doNothing().when(addSpaceMemberPort).addMemberAccess(isA(Long.class),
            isA(UUID.class), isA(UUID.class), isA(LocalDateTime.class));

        assertDoesNotThrow(() -> service.addMember(spaceId, email, currentUserId));

        verify(loadUserIdByEmailPort).loadByEmail(email);
        verify(loadSpacePort).getById(spaceId);
        verify(checkMemberSpaceAccessPort, times(2)).checkAccess(any(UUID.class));
        verify(addSpaceMemberPort).addMemberAccess(anyLong(), any(UUID.class), any(UUID.class), any(LocalDateTime.class));

    }
}
