package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupMembersServiceTest {

    @InjectMocks
    private GetExpertGroupMembersService service;
    @Mock
    private CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetExpertGroupMembers_ValidInputs_NoStatus_ValidResults() {
        UUID currentUserId = UUID.randomUUID();
        String expectedDownloadLink = "downloadLink";
        long expertGroupId = 123L;
        int page = 0;
        int size = 10;
        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(expertGroupId, null, currentUserId, size, page);
        LoadExpertGroupMembersPort.Member member1 = createPortResult(UUID.randomUUID());
        LoadExpertGroupMembersPort.Member member2 = createPortResult(UUID.randomUUID());
        List<LoadExpertGroupMembersPort.Member> portMembers = List.of(member1, member2);
        List<GetExpertGroupMembersUseCase.Member> expectedMembers = List.of(
            portToUseCaseResult(member1),
            portToUseCaseResult(member2)
        );

        PaginatedResponse<LoadExpertGroupMembersPort.Member> paginatedResult = new PaginatedResponse<>(portMembers, page, size, "title", "asc", 2);

        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(currentUserId);
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(expertGroupId, 1, page, size)).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        assertEquals(expectedMembers, result.getItems());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("title", result.getSort());
        assertEquals("asc", result.getOrder());
        assertEquals(portMembers.size(), result.getTotal());
    }

    @Test
    void testGetExpertGroupMembers_CurrentUserIsNotOwner_PendingStatus_EmptyResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        long expertGroupId = 123L;
        int page = 0;
        int size = 10;
        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(expertGroupId, ExpertGroupAccessStatus.PENDING.name(), currentUserId, size, page);

        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(ownerId);

        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPage());
        assertEquals(0, result.getSize());
        assertNull(result.getSort());
        assertNull(result.getOrder());
        assertEquals(0, result.getTotal());
    }

    @Test
    void testGetExpertGroupMembers_InvalidExpertGroupId_ExpertGroupNotFound() {
        UUID currentUserId = UUID.randomUUID();
        long expertGroupId = 123L;
        int page = 0;
        int size = 10;
        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(expertGroupId, null, currentUserId, size, page);

        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, ()->service.getExpertGroupMembers(param));
    }

    @Test
    void testGetExpertGroupMembers_CurrentUserIsNotOwner_ResultWithoutEmail() {
        UUID currentUserId = UUID.randomUUID();
        String expectedDownloadLink = "downloadLink";
        long expertGroupId = 123L;
        int page = 0;
        int size = 10;

        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(expertGroupId, null, currentUserId, size, page);
        LoadExpertGroupMembersPort.Member member1 = createPortResult(UUID.randomUUID());
        LoadExpertGroupMembersPort.Member member2 = createPortResult(UUID.randomUUID());
        List<LoadExpertGroupMembersPort.Member> portMembers = List.of(member1, member2);
        PaginatedResponse<LoadExpertGroupMembersPort.Member> paginatedResult = new PaginatedResponse<>(portMembers, page, size, "title", "asc", 2);

        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(currentUserId);
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(expertGroupId, 1, page, size)).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        assertNotNull(result.getItems());
        assertNotNull(result.getItems().stream().map(GetExpertGroupMembersUseCase.Member::email));
    }

    private LoadExpertGroupMembersPort.Member createPortResult(UUID memberId) {
        return new LoadExpertGroupMembersPort.Member(
            memberId,
            "email" + memberId + "@example.com",
            "Name" + memberId,
            "Bio" + memberId,
            "picture" + memberId + ".png",
            "http://www.example" + memberId + ".com",
            ExpertGroupAccessStatus.ACTIVE.ordinal(),
            LocalDateTime.now());
    }

    private GetExpertGroupMembersUseCase.Member portToUseCaseResult(LoadExpertGroupMembersPort.Member portMember) {
        String expectedDownloadLink = "downloadLink";
        return new GetExpertGroupMembersUseCase.Member(
            portMember.id(),
            portMember.email(),
            portMember.displayName(),
            portMember.bio(),
            expectedDownloadLink,
            portMember.linkedin(),
            ExpertGroupAccessStatus.values()[portMember.status()],
            portMember.inviteExpirationDate());
    }
}
