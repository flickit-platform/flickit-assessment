package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupServiceTest {

    @InjectMocks
    DeleteExpertGroupService service;
    @Mock
    DeleteExpertGroupPort deleteExpertGroupPort;
    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    CountExpertGroupKitsPort countExpertGroupKitsPort;

    @Test
    @DisplayName("Valid User and expert groupId")
    void testDeleteExpertGroup_validParameters_successful() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        CountExpertGroupKitsPort.Result result = new CountExpertGroupKitsPort.Result(0,0);

        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(anyLong())).thenReturn(currentUserId);
        when(countExpertGroupKitsPort.countKits(anyLong())).thenReturn(result);
        doNothing().when(deleteExpertGroupPort).deleteById(isA(Long.class));

        assertDoesNotThrow(() -> service.deleteExpertGroup(param));

        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(expertGroupId);
        verify(countExpertGroupKitsPort, times(1)).countKits(expertGroupId);
        verify(deleteExpertGroupPort, times(1)).deleteById(expertGroupId);
    }

    @Test
    @DisplayName("Not valid expert groupId")
    void testDeleteExpertGroup_inValidExpertGroup_expertGroupNotFoundException() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(anyLong())).thenThrow(new ResourceNotFoundException(""));

        assertThrows(ResourceNotFoundException.class, () -> service.deleteExpertGroup(param));

        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(expertGroupId);
        verifyNoInteractions(countExpertGroupKitsPort, deleteExpertGroupPort);
    }

    @Test
    @DisplayName("Valid User and expert groupId but having assessment kit")
    void testDeleteExpertGroup_expertGroupHavingKit_accessDenied() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);
        CountExpertGroupKitsPort.Result result = new CountExpertGroupKitsPort.Result(1,0);

        when(loadExpertGroupOwnerPort.loadOwnerId(anyLong())).thenReturn(currentUserId);
        when(countExpertGroupKitsPort.countKits(anyLong())).thenReturn(result);
        assertThrows(ValidationException.class, () -> service.deleteExpertGroup(param));

        result = new CountExpertGroupKitsPort.Result(1,1);
        when(countExpertGroupKitsPort.countKits(anyLong())).thenReturn(result);
        assertThrows(ValidationException.class, () -> service.deleteExpertGroup(param));

        result = new CountExpertGroupKitsPort.Result(0,1);
        when(countExpertGroupKitsPort.countKits(anyLong())).thenReturn(result);
        assertThrows(ValidationException.class, () -> service.deleteExpertGroup(param));

        verify(loadExpertGroupOwnerPort, times(3)).loadOwnerId(expertGroupId);
        verify(countExpertGroupKitsPort, times(3)).countKits(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPort);
    }
}
