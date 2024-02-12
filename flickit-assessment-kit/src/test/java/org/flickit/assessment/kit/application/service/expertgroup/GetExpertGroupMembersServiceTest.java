package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.CreateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMembersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GetExpertGroupMembersServiceTest {

    @InjectMocks
    private GetExpertGroupMembersService service;
    @Mock
    private LoadExpertGroupMembersPort loadExpertGroupMembersPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetExpertGroupMembers_Success() {
        int page = 1;
        int size = 10;
        long id = 123;

        PaginatedResponse<GetExpertGroupMembersUseCase.Member> expectedResult = new PaginatedResponse<>(
            Collections.singletonList(member),
            page,
            size,
            null,
            null,
            1
        );

        // Mock behavior
        when(loadExpertGroupMembersPort.loadExpertGroupMembers(any())).thenReturn(new PaginatedResponse<>(List.of(Mem),0,10) );
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any(Duration.class))).thenReturn("test-download-link");

        // Call the service method
        GetExpertGroupMembersUseCase.Param param = new GetExpertGroupMembersUseCase.Param(page, size, id);
        PaginatedResponse<GetExpertGroupMembersUseCase.Member> actualResult = service.getExpertGroupMembers(param);

        // Verify interactions

        ArgumentCaptor<LoadExpertGroupMembersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadExpertGroupMembersPort.Param.class);

        verify(loadExpertGroupMembersPort).loadExpertGroupMembers(loadPortParam.capture());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertEquals(id, loadPortParam.getValue().expertGroupId());
        verify(createFileDownloadLinkPort).createDownloadLink(mockResult.picture(), Duration.ofHours(1));

        // Verify the result
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    private final UUID currentUserId = UUID.randomUUID();
    private final GetExpertGroupMembersUseCase.Member member = new GetExpertGroupMembersUseCase.Member(
        UUID.randomUUID(),
        "Expert Group Bio",
        "Expert Group About",
        null,
        "http://www.example.com/sample.png",
        "http://www.example.com");
}
