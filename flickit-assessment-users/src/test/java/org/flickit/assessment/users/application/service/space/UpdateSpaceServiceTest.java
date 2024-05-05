package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.space.UpdateSpacePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

}
