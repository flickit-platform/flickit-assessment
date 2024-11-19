package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupMemberServiceTest {

    @InjectMocks
    private DeleteExpertGroupMemberService service;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    @Test
    void deleteMember_validParameter_successful() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        doNothing().when(deleteExpertGroupMemberPort).deleteMember(expertGroupId, userId);

        assertDoesNotThrow(() -> service.deleteMember(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(deleteExpertGroupMemberPort).deleteMember(expertGroupId, userId);
    }

    @Test
    void deleteMember_userNotMember_ResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        doThrow(new ResourceNotFoundException(""))
            .when(deleteExpertGroupMemberPort).deleteMember(expertGroupId, userId);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteMember(param));
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(deleteExpertGroupMemberPort).deleteMember(expertGroupId, userId);
    }

    @Test
    void deleteMember_expertGroupNotExist_AccessDeniedException() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> service.deleteMember(param));
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupMemberPort);
    }

    @Test
    void deleteMember_currentUserNotOwner_AccessDeniedException() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, () -> service.deleteMember(param));
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupMemberPort);
    }
}
