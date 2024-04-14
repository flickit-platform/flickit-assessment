package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.spaceaccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceaccess.CheckMemberSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.spaceaccess.AddSpaceMemberPort;
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
        var param = new AddSpaceMemberUseCase.Param(spaceId,email,currentUserId);
        var portResult = new Space(spaceId, "title", "Title",currentUserId);

        when(loadSpacePort.loadSpace(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(userId);
        when(checkMemberSpaceAccessPort.checkAccess(userId)).thenReturn(false);
        doNothing().when(addSpaceMemberPort).addMemberAccess(isA(AddSpaceMemberPort.Param.class));

        assertDoesNotThrow(() -> service.addMember(param));

        verify(loadSpacePort).loadSpace(spaceId);
        verify(checkMemberSpaceAccessPort, times(2)).checkAccess(any(UUID.class));
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verify(addSpaceMemberPort).addMemberAccess(any(AddSpaceMemberPort.Param.class));

    }

    @Test
    @DisplayName("Adding a member to an invalid space should cause ValidationException")
    void addSpaceMember_invalidSpace_ValidationException(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId,email,currentUserId);
        when(loadSpacePort.loadSpace(spaceId)).thenReturn(null);

        assertThrows(ValidationException.class, ()-> service.addMember(param));
        verify(loadSpacePort).loadSpace(spaceId);
        verifyNoInteractions(checkMemberSpaceAccessPort);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(addSpaceMemberPort);
    }

    @Test
    @DisplayName("Adding a member to a valid space should be done by a member; otherwise causes AccessDeniedException")
    void addSpaceMember_inviterIsNotSpaceMember_AccessDeniedExceptionException(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var portResult = new Space(spaceId, "title", "Title",currentUserId);
        var param = new AddSpaceMemberUseCase.Param(spaceId,email,currentUserId);

        when(loadSpacePort.loadSpace(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, ()-> service.addMember(param));
        verify(loadSpacePort).loadSpace(spaceId);
        verify(checkMemberSpaceAccessPort).checkAccess(currentUserId);
        verifyNoInteractions(loadUserIdByEmailPort);
        verifyNoInteractions(addSpaceMemberPort);
    }

    @Test
    @DisplayName("Adding a non-flickit user to a space should cause ResourceNotException")
    void addSpaceMember_inviteeIsNotFlickitUser_ResourceNotException(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId,email,currentUserId);
        var portResult = new Space(spaceId, "title", "Title",currentUserId);

        when(loadSpacePort.loadSpace(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenThrow(new ResourceNotFoundException(""));

        assertThrows(ResourceNotFoundException.class, ()-> service.addMember(param));
        verify(loadSpacePort).loadSpace(spaceId);
        verify(checkMemberSpaceAccessPort).checkAccess(currentUserId);
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verifyNoInteractions(addSpaceMemberPort);
    }

    @Test
    @DisplayName("Adding an already-member user to a space should cause ResourceAlreadyExistsException")
    void addSpaceMember_inviteeIsMember_ResourceAlreadyExistsException(){
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId,email,currentUserId);
        var portResult = new Space(spaceId, "title", "Title",currentUserId);

        when(loadSpacePort.loadSpace(spaceId)).thenReturn(portResult);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(true);
        when(loadUserIdByEmailPort.loadByEmail(email)).thenReturn(userId);
        when(checkMemberSpaceAccessPort.checkAccess(userId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, ()-> service.addMember(param));
        verify(loadSpacePort).loadSpace(spaceId);
        verify(checkMemberSpaceAccessPort, times(2)).checkAccess(any(UUID.class));
        verify(loadUserIdByEmailPort).loadByEmail(email);
        verifyNoInteractions(addSpaceMemberPort);
    }
}
