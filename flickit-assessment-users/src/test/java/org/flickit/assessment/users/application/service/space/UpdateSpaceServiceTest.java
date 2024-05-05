package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.space.UpdateSpacePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSpaceServiceTest {

    @InjectMocks
    UpdateSpaceService service;
    @Mock
    LoadSpacePort loadSpacePort;
    @Mock
    UpdateSpacePort updateSpacePort;

    @Test
    @DisplayName("If the space does not exist, updating space service should return ResourceNotFound")
    void testUpdateSpace_spaceNotExist_resourceNotFound() {
        long spaceId = 0L;
        String title = "Test";
        UUID currentUserId = UUID.randomUUID();
        UpdateSpaceUseCase.Param param = new UpdateSpaceUseCase.Param(spaceId, title, currentUserId);

        when(loadSpacePort.loadSpace(spaceId)).thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateSpace(param));
        assertEquals(SPACE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadSpacePort).loadSpace(anyLong());
        verifyNoInteractions(updateSpacePort);
    }

    @Test
    @DisplayName("Updating a space should be done only by the owner of the space")
    void testUpdateSpace_requesterIsNotOwner_AccessDeniedException(){
        long spaceId = 0L;
        String title = "Test";
        UUID currentUserId = UUID.randomUUID();
        UpdateSpaceUseCase.Param param = new UpdateSpaceUseCase.Param(spaceId, title, currentUserId);
        Space space = new Space(spaceId, "title", "Title", UUID.randomUUID(),
            LocalDateTime.now(),LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID());

        when(loadSpacePort.loadSpace(spaceId)).thenReturn(space);
        var throwable = assertThrows(AccessDeniedException.class, ()-> service.updateSpace(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadSpacePort).loadSpace(anyLong());
        verifyNoInteractions(updateSpacePort);
    }

    @Test
    @DisplayName("Updatin s dpsce with valid parametrs should be successful")
    void testUpdateSpace_validParameters_successful(){
        long spaceId = 0L;
        String title = "Test";
        UUID currentUserId = UUID.randomUUID();
        UpdateSpaceUseCase.Param param = new UpdateSpaceUseCase.Param(spaceId, title, currentUserId);
        Space space = new Space(spaceId, "title", "Title", currentUserId,
                LocalDateTime.now(),LocalDateTime.now(), UUID.randomUUID(), currentUserId);

        when(loadSpacePort.loadSpace(spaceId)).thenReturn(space);
        doNothing().when(updateSpacePort).updateSpace(any(UpdateSpacePort.Param.class));

        assertDoesNotThrow(()-> service.updateSpace(param));

        verify(loadSpacePort).loadSpace(anyLong());
        verify(updateSpacePort).updateSpace(any(UpdateSpacePort.Param.class));
    }
}
