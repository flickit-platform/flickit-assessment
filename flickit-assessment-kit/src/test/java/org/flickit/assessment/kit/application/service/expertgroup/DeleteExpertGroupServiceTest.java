package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupUsedByKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.DeleteExpertGroupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
    @Captor
    ArgumentCaptor<Long> expertGroupIdCaptor;
    @Captor
    ArgumentCaptor<UUID> currentUserIdCaptor;

    @Test
    public void testDeleteExpertGroup_validParameters_successful() {
        when(checkExpertGroupOwnerPort.checkIsOwner(any(Long.class), any(UUID.class))).thenReturn(true);
        when(checkExpertGroupUsedByKitPort.checkByKitId(any(Long.class))).thenReturn(false);
        doNothing().when(deleteExpertGroupPort).deleteById(isA(Long.class));

        service.deleteExpertGroup(expertGroupId, currentUserId);

        verify(checkExpertGroupUsedByKitPort).checkByKitId(expertGroupIdCaptor.capture());
        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.capture(),currentUserIdCaptor.capture());
        verify(deleteExpertGroupPort).deleteById(expertGroupIdCaptor.capture());

        verify(checkExpertGroupOwnerPort).checkIsOwner(expertGroupIdCaptor.getValue(), currentUserIdCaptor.getValue());
        verify(checkExpertGroupUsedByKitPort).checkByKitId(expertGroupIdCaptor.getValue());
        verify(deleteExpertGroupPort).deleteById(expertGroupIdCaptor.getValue());
    }

    long expertGroupId= 0L;
    UUID currentUserId = UUID.randomUUID();
}
