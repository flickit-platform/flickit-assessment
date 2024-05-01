package org.flickit.assessment.users.application.service.spaceinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceInviteesUseCase.Param;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInviteesPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.service.spaceuseraccess.GetSpaceMembersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class GetSpaceInviteesServiceTest {

    @InjectMocks
    GetSpaceInviteesService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;
    @Mock
    LoadSpaceInviteesPort loadSpaceInviteesPort;

    @Test
    @DisplayName("Only members can see the Space members")
    void testGetSpaceInvitees_spaceAccessNotFound_accessDeniedException() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getInvitees(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(checkSpaceAccessPort).checkIsMember(spaceId,currentUserId);
        verifyNoInteractions(loadSpaceInviteesPort);
    }
}
