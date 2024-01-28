package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateDslDownloadLinkPort;
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

    @InjectMocks
    private GetKitDslDownloadLinkService service;
    @Mock
    private LoadDslFilePathPort loadDslFilePathPort;
    @Mock
    private CreateDslDownloadLinkPort createDslDownloadLinkPort;
    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Test
    void getKitLink_whenDslFilePathExists_shouldReturnDownloadLink() {
        String expectedFilePath = "/path/to/dsl/file";
        when(loadDslFilePathPort.loadDslFilePath(param.getKitId()))
            .thenReturn(Optional.of(expectedFilePath));

        String expectedDownloadLink = "http://download.link";
        when(createDslDownloadLinkPort.createDownloadLink(expectedFilePath, EXPIRY_DURATION))
            .thenReturn(expectedDownloadLink);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId()))
            .thenReturn(true);
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId()))
            .thenReturn(expertGroupId);

        String result = service.getKitLink(param);
        assertEquals(expectedDownloadLink, result);

        verify(loadDslFilePathPort).loadDslFilePath(param.getKitId());
        verify(createDslDownloadLinkPort).createDownloadLink(expectedFilePath, EXPIRY_DURATION);
    }

    @Test
    void getKitLink_whenUserIsNotMember_shouldReturnDownloadLink() {
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId()))
            .thenReturn(false);
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId()))
            .thenReturn(expertGroupId);

        assertThrows(AccessDeniedException.class, () -> service.getKitLink(param));
    }

    @Test
    void getKitLink_whenDslFilePathDoesNotExist_shouldThrowResourceNotFoundException() {

        when(loadDslFilePathPort.loadDslFilePath(param.getKitId()))
            .thenReturn(null);

        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId()))
            .thenReturn(true);
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId()))
            .thenReturn(expertGroupId);

        when(loadDslFilePathPort.loadDslFilePath(param.getKitId()))
            .thenReturn(Optional.empty());
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId()))
            .thenReturn(true);
        assertThrows(ResourceNotFoundException.class, () -> service.getKitLink(param),
            GET_KIT_DSL_FILE_PATH_NOT_FOUND);
        verify(loadDslFilePathPort).loadDslFilePath(param.getKitId());
        verifyNoInteractions(createDslDownloadLinkPort);
    }

    private final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private final Param param = new Param(0L, UUID.randomUUID());
    private final long expertGroupId = 1;

}
