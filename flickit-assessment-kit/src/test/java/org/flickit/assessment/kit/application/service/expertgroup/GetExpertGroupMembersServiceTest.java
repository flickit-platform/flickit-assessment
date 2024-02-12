package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMembersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

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
        int page = 0;
        int size = 1;
        long id = 123;

        var member1 = createMember(UUID.randomUUID());
        var member2 = createMember(UUID.randomUUID());

        List<LoadExpertGroupMembersPort.Result> members = List.of(member1, member2);

        PaginatedResponse<LoadExpertGroupMembersPort.Result> portResult = new PaginatedResponse<>(
            members,
            page,
            size,
            null,
            null,
            2
        );

    }

    private LoadExpertGroupMembersPort.Result createMember(UUID memberId) {

        return new LoadExpertGroupMembersPort.Result(
            memberId,
            "Expert Group Bio",
            "Expert Group About",
            null,
            null,
            "http://www.example.com");
    }
}
