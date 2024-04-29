package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.DeleteSpaceMemberUseCase.Param;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceMemberServiceTest {

    @InjectMocks
    DeleteSpaceMemberService service;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Test
    @DisplayName("Deleting a member from space, should be done by owner")
    void testDeleteSpaceMember_invalidOwner_userNotAllowed(){
        long spaceId = 0L;
        var userId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        Param param = new Param(spaceId, userId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, ()-> service.deleteMember(param), COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
