package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.flickit.assessment.users.application.port.out.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpaceServiceTest {

    @InjectMocks
    GetSpaceService service;

    @Mock
    LoadSpaceDetailsPort loadSpaceDetailsPort;
    @Mock
    UpdateSpaceLastSeenPort updateSpaceLastSeenPort;

    @Test
    @DisplayName("When the current user is owner, 'GetSpaceService'  should set the 'editable' field as true")
    void testLoadSpaceService_isOwner_successFullWithIsOwnerTrue() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        GetSpaceUseCase.Param param = new GetSpaceUseCase.Param(spaceId, currentUserId);
        LoadSpaceDetailsPort.Result portResult = new LoadSpaceDetailsPort.Result(spaceId, "code", "title",
            currentUserId, LocalDateTime.now(), 1, 1);

        when(loadSpaceDetailsPort.loadSpace(spaceId, currentUserId)).thenReturn(portResult);
        doNothing().when(updateSpaceLastSeenPort).updateLastSeen(anyLong(), any(UUID.class), any(LocalDateTime.class));

        var result = service.getSpace(param);
        assertTrue(result.editable(), "'editable' should be true");
        verify(loadSpaceDetailsPort).loadSpace(anyLong(), any(UUID.class));
        verify(updateSpaceLastSeenPort).updateLastSeen(anyLong(), any(UUID.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("When the current user is not owner, 'GetSpaceService' should set the 'editable' field as false")
    void testLoadSpaceService_isNotOwner_successFullWithIsOwnerFalse() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        GetSpaceUseCase.Param param = new GetSpaceUseCase.Param(spaceId, currentUserId);
        LoadSpaceDetailsPort.Result portResult = new LoadSpaceDetailsPort.Result(spaceId, "code", "title",
            UUID.randomUUID(), LocalDateTime.now(), 1, 1);

        when(loadSpaceDetailsPort.loadSpace(spaceId, currentUserId)).thenReturn(portResult);
        doNothing().when(updateSpaceLastSeenPort).updateLastSeen(anyLong(), any(UUID.class), any(LocalDateTime.class));

        var result = service.getSpace(param);
        assertFalse(result.editable(), "'editable' should be false");
        verify(loadSpaceDetailsPort).loadSpace(anyLong(), any(UUID.class));
        verify(updateSpaceLastSeenPort).updateLastSeen(anyLong(), any(UUID.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("When the 'space' does not exist, 'get space service' should not execute the update last seen.")
    void testLoadSpaceService_spaceIsNotExists_dontRunUpdate() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        GetSpaceUseCase.Param param = new GetSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceDetailsPort.loadSpace(spaceId, currentUserId))
            .thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> service.getSpace(param));
        verify(loadSpaceDetailsPort).loadSpace(anyLong(), any(UUID.class));
        verifyNoInteractions(updateSpaceLastSeenPort);
    }

}
