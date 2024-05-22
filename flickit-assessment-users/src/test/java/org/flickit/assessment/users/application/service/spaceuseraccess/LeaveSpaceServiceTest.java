package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.DeleteSpaceMemberPort;
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
class LeaveSpaceServiceTest {

    @InjectMocks
    LeaveSpaceService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    DeleteSpaceMemberPort spaceMemberPort;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Test
    @DisplayName("If space is not exists or user does not access to space, service should throw AccessDeniedException")
    void testLeaveSpace_userDoesNotAccess_accessDeniedError(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceUseCase.Param param = new LeaveSpaceUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.leaveMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(spaceMemberPort, loadSpaceOwnerPort);
    }

    @Test
    @DisplayName("If there are valid inputs, but the userId is owner, it should throw AccessDenied")
    void testLeaveSpace_userIdIsOwner_success(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceUseCase.Param param = new LeaveSpaceUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);

        assertThrows(ValidationException.class, ()-> service.leaveMember(param));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verifyNoInteractions(spaceMemberPort);
    }

    @Test
    @DisplayName("If there are valid inputs, service should remove the access successfully")
    void testLeaveSpace_validParameters_success(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceUseCase.Param param = new LeaveSpaceUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);

        assertDoesNotThrow(()-> service.leaveMember(param));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verify(spaceMemberPort).delete(param.getId(), param.getCurrentUserId());
    }
}
