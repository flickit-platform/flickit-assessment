package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMembersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupMembersServiceTest {

    @InjectMocks
    private GetExpertGroupMembersService service;
    @Mock
    private LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;
    int count = 0;
    String expectedDownloadLink = "downloadLink";


    @Test
    void testGetExpertGroupMembers_ValidInputs_ValidResults() {

        int page = 0;
        int size = 10;
        var member1 = createPortResult(UUID.randomUUID());
        var member2 = createPortResult(UUID.randomUUID());
        long expertGroupId = 123L;

        List<LoadExpertGroupMembersPort.Result> portResults = List.of(member1, member2);

        List<GetExpertGroupMembersUseCase.Member> expectedMembers = List.of(
            portToUseCaseResult(member1),
            portToUseCaseResult(member2)
        );

        PaginatedResponse<LoadExpertGroupMembersPort.Result> paginatedResult = new PaginatedResponse<>(portResults, page, size, "title", "asc", 2);

        when(loadExpertGroupMembersPort.loadExpertGroupMembers(any())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class))).thenReturn(expectedDownloadLink);

        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(size, page, expertGroupId);
        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        ArgumentCaptor<LoadExpertGroupMembersPort.Param> captor = ArgumentCaptor.forClass(LoadExpertGroupMembersPort.Param.class);
        verify(loadExpertGroupMembersPort).loadExpertGroupMembers(captor.capture());

        assertEquals(page, captor.getValue().page());
        assertEquals(size, captor.getValue().size());
        assertEquals(expertGroupId, captor.getValue().expertGroupId());

        assertEquals(expectedMembers, result.getItems());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("title", result.getSort());
        assertEquals("asc", result.getOrder());
        assertEquals(portResults.size(), result.getTotal());
    }

    private LoadExpertGroupMembersPort.Result createPortResult(UUID memberId) {

        return new LoadExpertGroupMembersPort.Result(
            memberId,
            "email" + count + "@example.com",
            "Name" + count,
            "Bio" + count,
            "picture" + count + ".png",
            "http://www.example" + count + ".com");
    }

    private GetExpertGroupMembersUseCase.Member portToUseCaseResult(LoadExpertGroupMembersPort.Result portResult) {
        return new GetExpertGroupMembersUseCase.Member(
            portResult.id(),
            portResult.email(),
            portResult.displayNme(),
            portResult.bio(),
            expectedDownloadLink,
            portResult.linkedin()
        );
    }
}
