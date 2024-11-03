package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceUseCase;
import org.flickit.assessment.users.application.port.out.assessmentuserrole.DeleteSpaceAssessmentUserRolesPort;
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
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_SPACE_OWNER_NOT_ALLOWED;
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

    @Mock
    DeleteSpaceAssessmentUserRolesPort deleteSpaceAssessmentUserRolesPort;

    @Test
    @DisplayName("If current user is not a member of the space, service should throw AccessDeniedException")
    void testLeaveSpace_currentUserIsNotSpaceMember_accessDeniedError(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceUseCase.Param param = new LeaveSpaceUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.leaveSpace(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(spaceMemberPort, loadSpaceOwnerPort);
    }

    @Test
    @DisplayName("If current user is the owner of the space, it should throw ValidationException")
    void testLeaveSpace_currentUserIsSpaceOwner_ValidationException(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceUseCase.Param param = new LeaveSpaceUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);

        var throwable = assertThrows(ValidationException.class, ()-> service.leaveSpace(param));
        assertEquals(LEAVE_SPACE_OWNER_NOT_ALLOWED, throwable.getMessageKey());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verifyNoInteractions(spaceMemberPort);
    }

    @Test
    @DisplayName("If there are valid inputs, current user can leave the space successfully")
    void testLeaveSpace_validParameters_success(){
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        LeaveSpaceUseCase.Param param = new LeaveSpaceUseCase.Param(spaceId, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);

        assertDoesNotThrow(()-> service.leaveSpace(param));

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verify(deleteSpaceAssessmentUserRolesPort).delete(currentUserId, spaceId);
        verify(spaceMemberPort).delete(param.getId(), param.getCurrentUserId());
    }
}
