package org.flickit.assessment.kit.application.service.assessmentkit;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.application.service.kitdsl.GetKitDslDownloadLinkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DSL_DOWNLOAD_LINK_FILE_PATH_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitDslDownloadLinkServiceTest {

    @InjectMocks
    private GetKitDslDownloadLinkService service;
    @Mock
    private LoadDslFilePathPort loadDslFilePathPort;
    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;
    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    private final static Duration EXPIRY_DURATION = Duration.ofHours(1);

    @Test
    void testGetKitDSLDownloadLink_whenDslFilePathExists_shouldReturnDownloadLink() {
        Param param = new Param(0L, UUID.randomUUID());
        ExpertGroup expertGroup = createExpertGroup();

        String expectedFilePath = "/path/to/dsl/file";
        when(loadDslFilePathPort.loadDslFilePath(param.getKitId()))
            .thenReturn(Optional.of(expectedFilePath));

        String expectedDownloadLink = "http://download.link";
        when(createFileDownloadLinkPort.createDownloadLink(expectedFilePath, EXPIRY_DURATION))
            .thenReturn(expectedDownloadLink);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenReturn(expertGroup);

        String result = service.getKitDslDownloadLink(param);
        assertEquals(expectedDownloadLink, result);
    }

    @Test
    void testGetKitDSLDownloadLink_whenUserIsNotAllowed_shouldThrowException() {
        Param param = new Param(0L, UUID.randomUUID());
        ExpertGroup expertGroup = createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getKitDslDownloadLink(param));
        Assertions.assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testGetKitDSLDownloadLink_whenDslFilePathDoesNotExist_shouldThrowException() {
        Param param = new Param(0L, UUID.randomUUID());
        ExpertGroup expertGroup = createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenReturn(expertGroup);

        when(loadDslFilePathPort.loadDslFilePath(param.getKitId()))
            .thenReturn(Optional.empty());
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            .thenReturn(true);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitDslDownloadLink(param));
        Assertions.assertThat(throwable).hasMessage(GET_KIT_DSL_DOWNLOAD_LINK_FILE_PATH_NOT_FOUND);

        verifyNoInteractions(createFileDownloadLinkPort);
    }
}
