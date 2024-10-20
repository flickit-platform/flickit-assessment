package org.flickit.assessment.users.application.service.spaceinvitee;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceInviteesUseCase;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceInviteesUseCase.Param;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInviteesPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.test.fixture.application.SpaceInviteeMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpaceInviteesServiceTest {

    @InjectMocks
    GetSpaceInviteesService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    LoadSpaceInviteesPort loadSpaceInviteesPort;

    @Test
    @DisplayName("If space not found, it causes ResourceNotFound")
    void testGetSpaceInvitees_invalidSpaceId_ResourceNotFound() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadSpaceInviteesPort.loadInvitees(spaceId, page, size))
            .thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getInvitees(param));
        assertEquals(SPACE_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    @DisplayName("Only members can see the Space members")
    void testGetSpaceInvitees_spaceAccessNotFound_accessDeniedException() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getInvitees(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(loadSpaceInviteesPort);
    }

    @Test
    @DisplayName("Get Space Invitee service, for valid input should produce list of invitees")
    void testGetSpaceInvitees_validParameters_validInvitees() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        var invitee1 = SpaceInviteeMother.createSpaceInvitee(spaceId, "a1@b.c");

        var invitee2 = SpaceInviteeMother.createSpaceInvitee(spaceId, "a2@b.c");

        var invitees = List.of(invitee1, invitee2);
        var paginatedResponse = new PaginatedResponse<>(invitees, page, size, "SORT", "ORDER", invitees.size());

        GetSpaceInviteesUseCase.Param param = new GetSpaceInviteesUseCase.Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadSpaceInviteesPort.loadInvitees(spaceId, page, size)).thenReturn(paginatedResponse);

        var result = service.getInvitees(param);
        assertEquals(2, result.getItems().size(), "Items list should be empty");
        assertEquals(page, result.getPage(), "'page' should be 0");
        assertEquals(size, result.getSize(), "'size' should be 10");
        assertEquals(2, result.getTotal(), "'total' should be 2");

        verify(checkSpaceAccessPort).checkIsMember(spaceId,currentUserId);
        verify(loadSpaceInviteesPort).loadInvitees(spaceId, page, size);
    }
}
