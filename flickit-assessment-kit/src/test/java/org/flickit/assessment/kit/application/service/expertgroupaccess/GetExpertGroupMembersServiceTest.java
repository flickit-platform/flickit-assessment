package org.flickit.assessment.kit.application.service.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
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

    int count = 0;

    @Test
    void testGetExpertGroupMembers_ValidInputs_ValidResults() {
        String expectedDownloadLink = "downloadLink";

        PaginatedResponse<LoadExpertGroupMembersPort.Member> paginatedResult = new PaginatedResponse<>(portMembers, page, size, "title", "asc", 2);

        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(Optional.ofNullable(currentUserId));
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(paginatedResult);
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
    void testGetExpertGroupMembers_InvalidExpertGroupId_ExpertGroupNotFound() {
        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, ()->service.getExpertGroupMembers(param));
    }

    @Test
    void testGetExpertGroupMembers_CurrentUserIsNotOwner_ResultWithoutEmail() {
        String expectedDownloadLink = "downloadLink";

        PaginatedResponse<LoadExpertGroupMembersPort.Member> paginatedResult = new PaginatedResponse<>(portMembers, page, size, "title", "asc", 2);

        when(checkExpertGroupExistsPort.existsById(any(Long.class))).thenReturn(true);
        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(Optional.ofNullable(currentUserId));
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        assertNotNull(result.getItems());
        assertNotNull(result.getItems().stream().map(GetExpertGroupMembersUseCase.Member::email));
    }

    private LoadExpertGroupMembersPort.Member createPortResult(UUID memberId) {
        count++;
        return new LoadExpertGroupMembersPort.Member(
            memberId,
            "email" + count + "@example.com",
            "Name" + count,
            "Bio" + count,
            "picture" + count + ".png",
            "http://www.example" + count + ".com");
    }

    private GetExpertGroupMembersUseCase.Member portToUseCaseResult(LoadExpertGroupMembersPort.Member portMember) {
        String expectedDownloadLink = "downloadLink";
        return new GetExpertGroupMembersUseCase.Member(
            portMember.id(),
            portMember.email(),
            portMember.displayName(),
            portMember.bio(),
            expectedDownloadLink,
            portMember.linkedin()
        );
    }

    UUID currentUserId = UUID.randomUUID();
    long expertGroupId = 123L;
    int page = 0;
    int size = 10;
    GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(expertGroupId, currentUserId, size, page);
    LoadExpertGroupMembersPort.Member member1 = createPortResult(UUID.randomUUID());
    LoadExpertGroupMembersPort.Member member2 = createPortResult(UUID.randomUUID());
    List<LoadExpertGroupMembersPort.Member> portMembers = List.of(member1, member2);
    List<GetExpertGroupMembersUseCase.Member> expectedMembers = List.of(
        portToUseCaseResult(member1),
        portToUseCaseResult(member2)
    );
}
