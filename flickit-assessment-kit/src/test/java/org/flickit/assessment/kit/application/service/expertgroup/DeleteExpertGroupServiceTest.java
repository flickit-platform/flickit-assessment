package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupUsedByKitPort;
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
public class DeleteExpertGroupServiceTest {

    @InjectMocks
    DeleteExpertGroupService service;
    @Mock
    DeleteExpertGroupPort deleteExpertGroupPort;
    @Mock
    CheckExpertGroupOwnerPort checkExpertGroupOwnerPort;
    @Mock
    CheckExpertGroupUsedByKitPort checkExpertGroupUsedByKitPort;
    @Mock
    CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    @Captor
    ArgumentCaptor<Long> expertGroupIdCaptor;
    @Captor
    ArgumentCaptor<UUID> currentUserIdCaptor;

    @Test
    @DisplayName("Valid User and expert groupId")
    public void testDeleteExpertGroup_validParameters_successful() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(anyLong(), any(UUID.class))).thenReturn(true);
        when(checkExpertGroupUsedByKitPort.checkByKitId(anyLong())).thenReturn(false);
        doNothing().when(deleteExpertGroupPort).deleteById(isA(Long.class));

        assertDoesNotThrow(()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
        verify(checkExpertGroupUsedByKitPort).checkByKitId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
        verify(deleteExpertGroupPort).deleteById(expertGroupIdCaptor.capture());
    }

    @Test
    @DisplayName("Valid User and expert groupId")
    public void testDeleteExpertGroup_inValidExpertGroup_expertGroupNotFoundException() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
    }

    @Test
    @DisplayName("Expert group is used")
    public void testDeleteExpertGroup_usedExpertGroup_accessDenied() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(true);
        when(checkExpertGroupUsedByKitPort.checkByKitId(any(Long.class))).thenReturn(true);

        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
        verify(checkExpertGroupUsedByKitPort).checkByKitId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
    }

    @Test
    @DisplayName("User is not owner")
    public void testDeleteExpertGroup_userIsNotOwner_accessDenied() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(false);
        when(checkExpertGroupUsedByKitPort.checkByKitId(any(Long.class))).thenReturn(false);

        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
        verify(checkExpertGroupUsedByKitPort).checkByKitId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
    }

    @Test
    @DisplayName("User is not owner and expert group is used")
    public void testDeleteExpertGroup_userIsNotOwnerAndExpertGroupUsed_accessDenied() {
        when(checkExpertGroupExistsPort.existsById(anyLong())).thenReturn(true);
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(false);
        when(checkExpertGroupUsedByKitPort.checkByKitId(any(Long.class))).thenReturn(true);

        assertThrows(AccessDeniedException.class, ()-> service.deleteExpertGroup(expertGroupId, currentUserId));
        verify(checkExpertGroupExistsPort).existsById(expertGroupIdCaptor.capture());
        verify(checkExpertGroupUsedByKitPort).checkByKitId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
    }

    long expertGroupId= 0L;
    UUID currentUserId = UUID.randomUUID();
}
