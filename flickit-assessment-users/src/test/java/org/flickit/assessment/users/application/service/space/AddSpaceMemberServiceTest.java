package org.flickit.assessment.users.application.service.space;

import lombok.AllArgsConstructor;
import org.flickit.assessment.users.application.port.out.space.CheckMemberSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.users.application.port.out.space.AddSpaceMemberPort;
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
@AllArgsConstructor
class AddSpaceMemberServiceTest {

    @InjectMocks
    AddSpaceMemberService service;
    @Mock
    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    @Mock
    private final LoadSpacePort loadSpacePort;
    @Mock
    private final CheckMemberSpaceAccessPort checkMemberSpaceAccessPort;
    @Mock
    private final AddSpaceMemberPort addSpaceMemberPort;

    @Test
    @DisplayName("Adding a valid member to a valid space should cause a successful addition")
    void addSpaceMember_validParameters_successful(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var portResult = new LoadSpacePort.Result("spaceTitle");

        when(loadSpacePort.getById(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(checkMemberSpaceAccessPort.checkAccess(userId)).thenReturn(false);
        when(loadUserEmailByUserIdPort.loadEmail(currentUserId)).thenReturn(email);
        doNothing().when(addSpaceMemberPort).addMemberAccess(userId);

        assertDoesNotThrow(()-> service.addMember(spaceId, email,currentUserId));

        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadSpacePort).getById(spaceId);
        verify(checkMemberSpaceAccessPort, times(2)).checkAccess(any(UUID.class));
        verify(addSpaceMemberPort).addMemberAccess(userId);
    }

}
