package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase.Param;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.flickit.assessment.kit.application.service.kitdsl.GetKitDslDownloadLinkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DSL_FILE_PATH_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitDslDownloadLinkServiceTest {

    @Mock
    private LoadDslFilePathPort loadDslFilePathPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;
    @InjectMocks
    private GetKitDslDownloadLinkService service;
    private final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private final Param param = new Param(0L, UUID.randomUUID());

    @Test
    void getKitLink_whenDslFilePathExists_shouldReturnDownloadLink() {

        String expectedFilePath = "/path/to/dsl/file";
        when(loadDslFilePathPort.loadDslFilePath(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(Optional.of(expectedFilePath));

        String expectedDownloadLink = "http://download.link";
        when(createFileDownloadLinkPort.createDownloadLink(expectedFilePath, EXPIRY_DURATION))
            .thenReturn(expectedDownloadLink);

        String result = service.getKitLink(param);
        assertEquals(expectedDownloadLink, result);

        verify(loadDslFilePathPort).loadDslFilePath(param.getKitId(), param.getCurrentUserId());
        verify(createFileDownloadLinkPort).createDownloadLink(expectedFilePath, EXPIRY_DURATION);
    }

    @Test
    void getKitLink_whenDslFilePathDoesNotExist_shouldThrowResourceNotFoundException() {

        when(loadDslFilePathPort.loadDslFilePath(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getKitLink(param),
            GET_KIT_DSL_FILE_PATH_NOT_FOUND);
        verify(loadDslFilePathPort).loadDslFilePath(param.getKitId(), param.getCurrentUserId());
        verifyNoInteractions(createFileDownloadLinkPort);
    }
}
