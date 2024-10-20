package org.flickit.assessment.users.application.service.expertgroup;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.CountExpertGroupKitsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.DeleteExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_KITS_EXIST;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupServiceTest {

    @InjectMocks
    DeleteExpertGroupService service;

    @Mock
    DeleteExpertGroupPort deleteExpertGroupPort;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    CheckExpertGroupExistsPort checkExpertGroupExistsPort;

    @Mock
    CountExpertGroupKitsPort countExpertGroupKitsPort;

    @Test
    @DisplayName("Valid User and expertGroupId")
    void testDeleteExpertGroup_validParameters_successful() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        CountExpertGroupKitsPort.Result result = new CountExpertGroupKitsPort.Result(0, 0);

        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(countExpertGroupKitsPort.countKits(expertGroupId)).thenReturn(result);
        when(checkExpertGroupExistsPort.existsById(expertGroupId)).thenReturn(true);
        doNothing().when(deleteExpertGroupPort).deleteById(anyLong(),anyLong());

        assertDoesNotThrow(() -> service.deleteExpertGroup(param));

        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(expertGroupId);
        verify(countExpertGroupKitsPort, times(1)).countKits(expertGroupId);
        verify(deleteExpertGroupPort, times(1)).deleteById(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Not valid expert groupId")
    void testDeleteExpertGroup_invalidExpertGroup_expertGroupNotFoundException() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteExpertGroup(param));
        Assertions.assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);

        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(expertGroupId);
        verifyNoInteractions(countExpertGroupKitsPort, deleteExpertGroupPort);
    }

    @Test
    @DisplayName("Valid User and expertGroupId but having assessment kit")
    void testDeleteExpertGroup_expertGroupHavingKit_accessDenied() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);
        CountExpertGroupKitsPort.Result result = new CountExpertGroupKitsPort.Result(1, 0);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(countExpertGroupKitsPort.countKits(expertGroupId)).thenReturn(result);
        when(checkExpertGroupExistsPort.existsById(expertGroupId)).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteExpertGroup(param));
        assertEquals(DELETE_EXPERT_GROUP_KITS_EXIST, throwable.getMessageKey());

        result = new CountExpertGroupKitsPort.Result(1, 1);
        when(countExpertGroupKitsPort.countKits(anyLong())).thenReturn(result);

        throwable = assertThrows(ValidationException.class, () -> service.deleteExpertGroup(param));
        assertEquals(DELETE_EXPERT_GROUP_KITS_EXIST, throwable.getMessageKey());

        result = new CountExpertGroupKitsPort.Result(0, 1);
        when(countExpertGroupKitsPort.countKits(anyLong())).thenReturn(result);

        throwable = assertThrows(ValidationException.class, () -> service.deleteExpertGroup(param));
        assertEquals(DELETE_EXPERT_GROUP_KITS_EXIST, throwable.getMessageKey());

        verify(loadExpertGroupOwnerPort, times(3)).loadOwnerId(expertGroupId);
        verify(countExpertGroupKitsPort, times(3)).countKits(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPort);
    }

    @Test
    @DisplayName("Deleted expertGroup")
    void testDeleteExpertGroup_deletedExpertGroup_expertGroupNotFoundException() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(checkExpertGroupExistsPort.existsById(expertGroupId)).thenReturn(false);

        verifyNoInteractions(countExpertGroupKitsPort, deleteExpertGroupPort, loadExpertGroupOwnerPort);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteExpertGroup(param));
        Assertions.assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);
    }
}
