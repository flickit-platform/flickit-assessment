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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetExpertGroupMembersServiceTest {

    @InjectMocks
    private GetExpertGroupMembersService service;
    @Mock
    private LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;


    @Test
    void testGetExpertGroupMembers_ValidInputs_ValidResults() {


        // Test data
        int page = 0;
        int size = 10;
        UUID member1id = UUID.randomUUID();
        UUID member2id = UUID.randomUUID();
        long groupId = 123L;

        List<LoadExpertGroupMembersPort.Result> portResults = List.of(
            new LoadExpertGroupMembersPort.Result(member1id, "email1@example.com", "John Doe", "Bio 1", "picture1.jpg", "linkedin1"),
            new LoadExpertGroupMembersPort.Result(member2id, "email2@example.com", "Jane Smith", "Bio 2", "picture2.jpg", "linkedin2")
        );

        List<GetExpertGroupMembersUseCase.Member> expectedMembers = List.of(
            new GetExpertGroupMembersUseCase.Member(member1id, "email1@example.com", "John Doe", "Bio 1", "downloadLink1", "linkedin1"),
            new GetExpertGroupMembersUseCase.Member(member2id, "email2@example.com", "Jane Smith", "Bio 2", "downloadLink2", "linkedin2")
        );

        PaginatedResponse<LoadExpertGroupMembersPort.Result> paginatedResult = new PaginatedResponse<>(portResults, page, size, "name", "asc", 2);
        PaginatedResponse<GetExpertGroupMembersUseCase.Member> paginatedMember = new PaginatedResponse<>(expectedMembers, page, size, "name", "asc", 2);


        // Mock behavior
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(any())).thenReturn(paginatedResult);
        when(createFileDownloadLinkPort.createDownloadLink(eq("picture1.jpg"), any(Duration.class))).thenReturn("downloadLink1");
        when(createFileDownloadLinkPort.createDownloadLink(eq("picture2.jpg"), any(Duration.class))).thenReturn("downloadLink2");

        // Execute the service method
        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(size, page, groupId);
        PaginatedResponse<GetExpertGroupMembersUseCase.Member> result = service.getExpertGroupMembers(param);

        // Verify the interactions and assertions
        ArgumentCaptor<LoadExpertGroupMembersPort.Param> captor = ArgumentCaptor.forClass(LoadExpertGroupMembersPort.Param.class);
        verify(loadExpertGroupMembersPort).loadExpertGroupMembers(captor.capture());

        assertEquals(page, captor.getValue().page());
        assertEquals(size, captor.getValue().size());
        assertEquals(groupId, captor.getValue().expertGroupId());

        assertEquals(expectedMembers, result.getItems());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("name", result.getSort());
        assertEquals("asc", result.getOrder());
        assertEquals(portResults.size(), result.getTotal());
        assertEquals(paginatedMember, service.getExpertGroupMembers(new GetExpertGroupMembersUseCase.Param(size, page, groupId)));
    }
}
