package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.DeleteSpaceMemberUseCase.Param;
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
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_USER_ID_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_USER_IS_SPACE_OWNER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceMemberServiceTest {

    @InjectMocks
    DeleteSpaceMemberService service;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    DeleteSpaceMemberPort deleteSpaceMemberPort;

    @Mock
    DeleteSpaceAssessmentUserRolesPort deleteSpaceAssessmentUserRolesPort;

    @Test
    @DisplayName("Deleting a member from space, should be done by owner")
    void testDeleteSpaceMember_invalidOwner_userNotAllowed(){
        long spaceId = 0L;
        var userId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, userId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, ()-> service.deleteMember(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verifyNoInteractions(checkSpaceAccessPort);
        verifyNoInteractions(deleteSpaceMemberPort);
    }

    @Test
    @DisplayName("Owner might not remove himself/herself from the space")
    void testDeleteSpaceMember_ownerDelete_accessDenied() {
        long spaceId = 0L;
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, currentUserId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteMember(param));
        assertEquals(DELETE_SPACE_MEMBER_USER_IS_SPACE_OWNER, throwable.getMessageKey());

        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verifyNoInteractions(checkSpaceAccessPort);
        verifyNoInteractions(deleteSpaceMemberPort);
    }

    @Test
    @DisplayName("Deleting a member from space, user is not a member to delete")
    void testDeleteSpaceMember_invalidUserId_resourceNotFound(){
        long spaceId = 0L;
        var userId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, userId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()-> service.deleteMember(param), DELETE_SPACE_MEMBER_USER_ID_NOT_FOUND);
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verify(checkSpaceAccessPort).checkIsMember(spaceId, userId);
        verifyNoInteractions(deleteSpaceMemberPort);
    }

    @Test
    @DisplayName("Deleting a member from space, with valid parameters should be successful")
    void testDeleteSpaceMember_validParameters_successful(){
        long spaceId = 0L;
        var userId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, userId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        when(checkSpaceAccessPort.checkIsMember(spaceId, userId)).thenReturn(true);
        doNothing().when(deleteSpaceMemberPort).delete(spaceId, userId);

        assertDoesNotThrow(() -> service.deleteMember(param));
        verify(loadSpaceOwnerPort).loadOwnerId(spaceId);
        verify(checkSpaceAccessPort).checkIsMember(spaceId, userId);
        verify(deleteSpaceAssessmentUserRolesPort).delete(userId, spaceId);
        verify(deleteSpaceMemberPort).delete(spaceId, userId);
    }
}
