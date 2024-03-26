package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CheckExpertGroupHavingKitPort;
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

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupServiceTest {

    @InjectMocks
    DeleteExpertGroupService service;
    @Mock
    DeleteExpertGroupPort deleteExpertGroupPort;
    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    CheckExpertGroupHavingKitPort checkExpertGroupHavingKitPort;

    @Test
    @DisplayName("Valid User and expert groupId")
    void testDeleteExpertGroup_validParameters_successful() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(anyLong())).thenReturn(currentUserId);
        when(checkExpertGroupHavingKitPort.checkHavingKit(anyLong())).thenReturn(false);
        doNothing().when(deleteExpertGroupPort).deleteById(isA(Long.class));

        assertDoesNotThrow(() -> service.deleteExpertGroup(param));
    }

    @Test
    @DisplayName("Valid User and expert groupId")
    void testDeleteExpertGroup_expertGroupHavingKit_accessDenied() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(anyLong())).thenReturn(currentUserId);
        when(checkExpertGroupHavingKitPort.checkHavingKit(anyLong())).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> service.deleteExpertGroup(param));
    }

    @Test
    @DisplayName("Not valid expert groupId")
    void testDeleteExpertGroup_inValidExpertGroup_expertGroupNotFoundException() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupUseCase.Param param = new DeleteExpertGroupUseCase.Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(anyLong())).thenThrow(new ResourceNotFoundException(""));

        assertThrows(ResourceNotFoundException.class, () -> service.deleteExpertGroup(param));
    }
}
