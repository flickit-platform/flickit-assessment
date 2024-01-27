package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase.Param;
import org.flickit.assessment.kit.application.port.out.kitdsl.CheckIsMemberPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateDslDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateDslFileDownloadLinkPort;
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
    private CreateDslFileDownloadLinkPort createDslFileDownloadLinkPort;
    @Mock
    private CreateDslDownloadLinkPort createDslDownloadLinkPort;
    @Mock
    private CheckIsMemberPort checkIsMemberPort;
    @InjectMocks
    private GetKitDslDownloadLinkService service;
    private final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private final Param param = new Param(0L, UUID.randomUUID());

    @Test
    void getKitLink_whenDslFilePathExists_shouldReturnDownloadLink() {

        String expectedFilePath = "/path/to/dsl/file";
        when(createDslFileDownloadLinkPort.loadDslFilePath(param.getKitId()))
            .thenReturn(Optional.of(expectedFilePath));

        String expectedDownloadLink = "http://download.link";
        when(createDslDownloadLinkPort.createDownloadLink(expectedFilePath, EXPIRY_DURATION))
            .thenReturn(expectedDownloadLink);
        when(checkIsMemberPort.checkIsMemberByKitId(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(true);

        String result = service.getKitLink(param);
        assertEquals(expectedDownloadLink, result);

        verify(createDslFileDownloadLinkPort).loadDslFilePath(param.getKitId());
        verify(createDslDownloadLinkPort).createDownloadLink(expectedFilePath, EXPIRY_DURATION);
    }

    @Test
    void getKitLink_whenUserIsNotMember_shouldReturnDownloadLink() {

        when(checkIsMemberPort.checkIsMemberByKitId(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> service.getKitLink(param));
    }

    @Test
    void getKitLink_whenDslFilePathDoesNotExist_shouldThrowResourceNotFoundException() {

        when(createDslFileDownloadLinkPort.loadDslFilePath(param.getKitId()))
            .thenReturn(Optional.empty());
        when(checkIsMemberPort.checkIsMemberByKitId(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(true);
        assertThrows(ResourceNotFoundException.class, () -> service.getKitLink(param),
            GET_KIT_DSL_FILE_PATH_NOT_FOUND);
        verify(createDslFileDownloadLinkPort).loadDslFilePath(param.getKitId());
        verifyNoInteractions(createDslDownloadLinkPort);
    }
}
