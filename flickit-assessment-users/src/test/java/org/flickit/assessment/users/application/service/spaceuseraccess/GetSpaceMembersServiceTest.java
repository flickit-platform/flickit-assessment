package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.GetSpaceMembersUseCase.Param;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.LoadSpaceMembersPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpaceMembersServiceTest {

    @InjectMocks
    GetSpaceMembersService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    LoadSpaceMembersPort loadSpaceMembersPort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetSpaceMember_userDoesNotHaveAccess_accessDeniedException() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getSpaceMembers(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId,currentUserId);
        verifyNoInteractions(loadSpaceMembersPort);
        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetSpaceMember_validParameters_validMembers() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        int size = 10;
        int page = 0;
        var member1 = new LoadSpaceMembersPort.Member(userId1,
            "a@b.c", "name", "bio", true, "pictureLink", "linkedin");

        var member2 = new LoadSpaceMembersPort.Member(userId2,
            "a1@b.c", "name1","bio1", false, "pictureLink1", "linkedin1");

        var members = List.of(member1, member2);
        var paginatedResponse = new PaginatedResponse<>(members, page, size, "SORT", "ORDER", members.size());

        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadSpaceMembersPort.loadSpaceMembers(spaceId, page, size)).thenReturn(paginatedResponse);
        when(createFileDownloadLinkPort.createDownloadLinkSafe(anyString(), any())).thenReturn("pictureLink");

        var result = service.getSpaceMembers(param);
        assertEquals(2, result.getItems().size());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(2, result.getTotal());
        assertTrue(result.getItems().getFirst().isOwner());
        assertFalse(result.getItems().get(1).isOwner());

        verify(checkSpaceAccessPort).checkIsMember(spaceId,currentUserId);
        verify(loadSpaceMembersPort).loadSpaceMembers(spaceId, page, size);
        verify(createFileDownloadLinkPort, times(2)).createDownloadLinkSafe(any(String.class), any(Duration.class));
    }
}
