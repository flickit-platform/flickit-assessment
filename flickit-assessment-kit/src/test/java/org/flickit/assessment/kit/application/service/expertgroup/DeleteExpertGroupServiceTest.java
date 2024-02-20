package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupUsedByKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.DeleteExpertGroupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void testDeleteExpertGroup_validParameters_successful(){
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(true);
        when(checkExpertGroupUsedByKitPort.checkByKitId(any(Long.class))).thenReturn(false);
        doNothing().when(deleteExpertGroupPort).deleteById(isA(Long.class));

        assertDoesNotThrow(()-> service.deleteExpertGroup(expertGroupId,currentUserId));

        verify(checkExpertGroupOwnerPort,times(1)).checkIsOwner(any(Long.class), any(UUID.class));
        verify(checkExpertGroupUsedByKitPort,times(1)).checkByKitId(any(Long.class));
        verify(deleteExpertGroupPort,times(1)).deleteById(any(Long.class));
    }

    final static long expertGroupId =  123L;
    final static UUID currentUserId = UUID.randomUUID();
}
