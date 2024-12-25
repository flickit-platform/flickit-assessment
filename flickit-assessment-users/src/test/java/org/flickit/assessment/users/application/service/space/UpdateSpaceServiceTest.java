package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.space.UpdateSpacePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSpaceServiceTest {

    @InjectMocks
    UpdateSpaceService service;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    UpdateSpacePort updateSpacePort;

    @Test
    @DisplayName("If the space does not exist, updating space should return ResourceNotFound")
    void testUpdateSpace_spaceNotExist_resourceNotFound() {
        long spaceId = 0L;
        String title = "Test";
        UUID currentUserId = UUID.randomUUID();
        UpdateSpaceUseCase.Param param = new UpdateSpaceUseCase.Param(spaceId, title, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateSpace(param));
        assertEquals(SPACE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadSpaceOwnerPort).loadOwnerId(anyLong());
        verifyNoInteractions(updateSpacePort);
    }

    @Test
    @DisplayName("Updating a space should be done only by the owner of the space")
    void testUpdateSpace_requesterIsNotOwner_AccessDeniedException(){
        long spaceId = 0L;
        String title = "Test";
        UUID currentUserId = UUID.randomUUID();
        UpdateSpaceUseCase.Param param = new UpdateSpaceUseCase.Param(spaceId, title, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(UUID.randomUUID());
        var throwable = assertThrows(AccessDeniedException.class, ()-> service.updateSpace(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadSpaceOwnerPort).loadOwnerId(anyLong());
        verifyNoInteractions(updateSpacePort);
    }

    @Test
    @DisplayName("Updating a space with valid parameters should be successful")
    void testUpdateSpace_validParameters_successful(){
        long spaceId = 0L;
        String title = "Test";
        UUID currentUserId = UUID.randomUUID();
        UpdateSpaceUseCase.Param param = new UpdateSpaceUseCase.Param(spaceId, title, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        doNothing().when(updateSpacePort).updateSpace(any(UpdateSpacePort.Param.class));

        assertDoesNotThrow(()-> service.updateSpace(param));

        ArgumentCaptor<UpdateSpacePort.Param> captor = ArgumentCaptor.forClass(UpdateSpacePort.Param.class);
        verify(updateSpacePort).updateSpace(captor.capture());
        assertEquals(spaceId, captor.getValue().id());
        assertEquals("test", captor.getValue().code());
        assertEquals(title, captor.getValue().title());
        assertEquals(currentUserId, captor.getValue().lastModifiedBy());
        assertNotNull(captor.getValue().lastModificationTime());
    }
}
