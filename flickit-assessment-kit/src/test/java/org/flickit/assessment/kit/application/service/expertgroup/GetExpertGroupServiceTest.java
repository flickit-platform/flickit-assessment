package org.flickit.assessment.kit.application.service.expertgroup;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupServiceTest {

    @InjectMocks
    private GetExpertGroupService service;
    @Mock
    private LoadExpertGroupPort loadExpertGroupPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetExpertGroup_ValidInputs_ValidResults() {

        var portResult = createPortResult(expertGroupId, currentUserId);
        var useCaseResul = portToUseCaseResult(portResult);

        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class)))
            .thenReturn("/path/to/picture");

        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenReturn(portResult);

        var param = new GetExpertGroupUseCase.Param(expertGroupId, currentUserId);
        var result = service.getExpertGroup(param);

        ArgumentCaptor<Long> loadPortParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupPort).loadExpertGroup(loadPortParam.capture());

        assertNotNull(result);
        assertNotNull(result.getPicture());
        assertEquals(useCaseResul.getId(), result.getId());
        assertTrue(result.isEditable());
    }

    @Test
    void testGetExpertGroup_NullPicture_ValidResults() {

        var portResult = createPortResult(expertGroupId, currentUserId);
        var useCaseResul = portToUseCaseResult(portResult);

        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class)))
            .thenReturn(null);

        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenReturn(portResult);

        var param = new GetExpertGroupUseCase.Param(expertGroupId, currentUserId);
        var result = service.getExpertGroup(param);

        ArgumentCaptor<Long> loadPortParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupPort).loadExpertGroup(loadPortParam.capture());

        assertNotNull(result);
        assertNull(result.getPicture());
        assertEquals(useCaseResul.getId(), result.getId());
        assertTrue(result.isEditable());
    }

    @Test
    void testGetExpertGroup_ValidInputs_emptyResults() {
        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenThrow(new ResourceNotFoundException("message"));

        var param = new GetExpertGroupUseCase.Param(expertGroupId, currentUserId);

        assertThrows(ResourceNotFoundException.class, () -> service.getExpertGroup(param));
    }

    long expertGroupId = new Random().nextLong();
    static UUID currentUserId = UUID.randomUUID();

    private static LoadExpertGroupPort.Result createPortResult(long id, UUID ownerId) {
        return new LoadExpertGroupPort.Result(id,
            "Title" + id,
            "Bio" + id,
            "About" + id,
            "Picture" + id,
            "Website" + id,
            ownerId);
    }

    private static ExpertGroup portToUseCaseResult(LoadExpertGroupPort.Result portResult) {
        return new ExpertGroup(
            portResult.id(),
            portResult.title(),
            portResult.bio(),
            portResult.about(),
            portResult.picture(),
            portResult.website(),
            portResult.ownerId().equals(currentUserId)
        );
    }
}

