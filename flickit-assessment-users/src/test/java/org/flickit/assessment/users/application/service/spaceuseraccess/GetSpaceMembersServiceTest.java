package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.GetSpaceMembersUseCase.Param;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.LoadSpaceMembersPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
    @DisplayName("Only members can see the Space members")
    void testGetSpaceMember_spaceAccessNotFound_emptyResponse() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var result = service.getSpaceMembers(param);
        assertEquals(0, result.getItems().size(), "Items list should be empty");
        assertEquals(0, result.getTotal(), "'page' should be 0");
        assertEquals(0, result.getTotal(), "'size' should be 0");
        assertEquals(0, result.getTotal(), "'total' should be 0");
    }

    @Test
    @DisplayName("Only members can see the Space members")
    void testGetSpaceMember_validParameters_validMembers() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        var member1 = new LoadSpaceMembersPort.Member(UUID.randomUUID(),
            "a@b.c", "name", "bio", "pictureLink", "linkedin");

        var member2 = new LoadSpaceMembersPort.Member(UUID.randomUUID(),
            "a1@b.c", "name1", "bio1", "pictureLink1", "linkedin1");

        var members = List.of(member1, member2);
        var paginatedResponse = new PaginatedResponse<>(members, page, size, "SORT", "ORDER", members.size());

        Param param = new Param(spaceId, currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadSpaceMembersPort.loadSpaceMembers(spaceId, page, size)).thenReturn(paginatedResponse);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any())).thenReturn("pictureLink");

        var result = service.getSpaceMembers(param);
        assertEquals(2, result.getItems().size(), "Items list should be empty");
        assertEquals(page, result.getPage(), "'page' should be 0");
        assertEquals(size, result.getSize(), "'size' should be 10");
        assertEquals(2, result.getTotal(), "'total' should be 2");
    }
}
