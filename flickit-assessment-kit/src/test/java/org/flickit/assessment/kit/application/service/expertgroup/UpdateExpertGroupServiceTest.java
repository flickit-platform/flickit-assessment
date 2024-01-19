package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.UpdateExpertGroupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExpertGroupServiceTest {

    @InjectMocks
    private UpdateExpertGroupService service;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private UpdateExpertGroupPort updateExpertGroupPort;
    @Mock
    private CheckExpertGroupIdPort checkExpertGroupIdPort;

    @Test
    void testUpdateExpertGroup_ValidParams_UpdateExpertGroupPortCalled() {
        UUID expertGroupOwnerId = currentUserId;
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(expertGroupOwnerId));
        when(checkExpertGroupIdPort.existsById(expertGroupId)).thenReturn(true);
        service.updateExpertGroup(param);
        verify(updateExpertGroupPort, times(1)).update(any());
    }

    @Test
    void testUpdateExpertGroup_InvalidCurrentUser_ThrowsAccessDeniedException() {
        UUID expertGroupOwnerId = UUID.randomUUID();
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(expertGroupOwnerId));
        when(checkExpertGroupIdPort.existsById(expertGroupId)).thenReturn(true);
        assertThrows(AccessDeniedException.class, () -> service.updateExpertGroup(param));
    }

    @Test
    void testUpdateExpertGroup_InvalidExpertGroupId_ThrowsResourceNotFoundException() {
        when(checkExpertGroupIdPort.existsById(expertGroupId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.updateExpertGroup(param));
    }

    long expertGroupId = 1L;
    UUID currentUserId = UUID.randomUUID();
    UpdateExpertGroupUseCase.Param param = new UpdateExpertGroupUseCase.Param(
        expertGroupId,
        "NewName",
        "NewAbout",
        "NewPicture",
        null,
        "NewBio",
        currentUserId
    );
}
