package org.flickit.assessment.kit.application.service.expertgroupaccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.ExistsExpertGroupByIdPort;
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
    private ExistsExpertGroupByIdPort existsExpertGroupByIdPort;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetExpertGroupMembers_ValidInputs_ValidResults() {

        long expertGroupId = 123L;
        int page = 0;
        int size = 10;
        var member1 = createPortResult(UUID.randomUUID());
        var member2 = createPortResult(UUID.randomUUID());

        List<LoadExpertGroupMembersPort.Member> portMembers = List.of(member1, member2);

        List<GetExpertGroupMembersUseCase.Member> expectedMembers = List.of(
            portToUseCaseResult(member1),
            portToUseCaseResult(member2)
        );

        PaginatedResponse<LoadExpertGroupMembersPort.Member> paginatedResult = new PaginatedResponse<>(portMembers, page, size, "title", "asc", 2);

        when(existsExpertGroupByIdPort.existsById(any(Long.class))).thenReturn(true);
        when(loadExpertGroupOwnerPort.loadOwnerId(any(Long.class))).thenReturn(Optional.ofNullable(currentUserId));
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(expertGroupId, currentUserId, size, page);
        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        assertEquals(expectedMembers, result.getItems());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("title", result.getSort());
        assertEquals("asc", result.getOrder());
        assertEquals(portMembers.size(), result.getTotal());
    }

    private LoadExpertGroupMembersPort.Member createPortResult(UUID memberId) {

        return new LoadExpertGroupMembersPort.Member(
            memberId,
            "email" + count + "@example.com",
            "Name" + count,
            "Bio" + count,
            "picture" + count + ".png",
            "http://www.example" + count + ".com");
    }

    private GetExpertGroupMembersUseCase.Member portToUseCaseResult(LoadExpertGroupMembersPort.Member portMember) {
        return new GetExpertGroupMembersUseCase.Member(
            portMember.id(),
            portMember.email(),
            portMember.displayName(),
            portMember.bio(),
            expectedDownloadLink,
            portMember.linkedin()
        );
    }

    int count = 0;
    String expectedDownloadLink = "downloadLink";
    UUID currentUserId = UUID.randomUUID();
}
