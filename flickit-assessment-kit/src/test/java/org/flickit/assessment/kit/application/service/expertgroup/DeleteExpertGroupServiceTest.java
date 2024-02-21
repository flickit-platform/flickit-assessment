package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CheckKitUsedByExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.DeleteExpertGroupPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupServiceTest {

    @InjectMocks
    DeleteExpertGroupService service;
    @Mock
    DeleteExpertGroupPort deleteExpertGroupPort;
    @Mock
    CheckExpertGroupOwnerPort checkExpertGroupOwnerPort;
    @Mock
    CheckKitUsedByExpertGroupPort checkKitUsedByExpertGroupPort;
    @Mock
    CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    @Captor
    ArgumentCaptor<Long> expertGroupIdCaptor;
    @Captor
    ArgumentCaptor<UUID> currentUserIdCaptor;

    @Test
    @DisplayName("Valid User and expert groupId")
    void testDeleteExpertGroup_validParameters_successful() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(anyLong(), any(UUID.class))).thenReturn(true);
        when(checkKitUsedByExpertGroupPort.checkKitUsedByExpertGroupId(anyLong())).thenReturn(false);
        doNothing().when(deleteExpertGroupPort).deleteById(isA(Long.class));

        assertDoesNotThrow(()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
        verify(checkKitUsedByExpertGroupPort).checkKitUsedByExpertGroupId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
        verify(deleteExpertGroupPort).deleteById(expertGroupIdCaptor.capture());
    }

    @Test
    @DisplayName("Not valid expert groupId")
    void testDeleteExpertGroup_inValidExpertGroup_expertGroupNotFoundException() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
    }

    @Test
    @DisplayName("ExpertGroupId and/or owner is not valid")
    void testDeleteExpertGroup_invalidParameters_accessDenied() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(true);
        when(checkKitUsedByExpertGroupPort.checkKitUsedByExpertGroupId(any(Long.class))).thenReturn(true);
        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));

        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(false);
        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));

        when(checkKitUsedByExpertGroupPort.checkKitUsedByExpertGroupId(any(Long.class))).thenReturn(false);
        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));

        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(false);
        when(checkKitUsedByExpertGroupPort.checkKitUsedByExpertGroupId(any(Long.class))).thenReturn(true);
        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));

        verify(checkExpertGroupExistsPort,times(4)).existsById(expertGroupIdCaptor.capture());
        verify(checkKitUsedByExpertGroupPort,times(4)).checkKitUsedByExpertGroupId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort,times(4)).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
    }

    long expertGroupId= 0L;
    UUID currentUserId = UUID.randomUUID();
}
