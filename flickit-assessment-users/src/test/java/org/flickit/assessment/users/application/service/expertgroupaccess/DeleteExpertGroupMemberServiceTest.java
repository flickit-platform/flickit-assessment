package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private Param param = new Param(0L, UUID.randomUUID(), UUID.randomUUID());

    @Test
    void testDeleteExpertGroupMember_validParameter_successful() {
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(param.getCurrentUserId());

        service.deleteMember(param);

        verify(deleteExpertGroupMemberPort).deleteMember(param.getExpertGroupId(), param.getUserId());
    }

    @Test
    void testDeleteExpertGroupMember_userIsOwner_AccessDeniedException() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        param = new Param(expertGroupId, currentUserId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testDeleteExpertGroupMember_currentUserIsNotOwner_AccessDeniedException() {
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteExpertGroupMemberPort);
    }
}
