package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND;
import static org.flickit.assessment.users.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        UUID currentUserId = UUID.randomUUID();
        ExpertGroup expertGroup = createExpertGroup("/path/to/picture", currentUserId);
        long expertGroupId = expertGroup.getId();

        String pictureLink = "cdn.flickit.org" + expertGroup.getPicture();
        when(createFileDownloadLinkPort.createDownloadLink(any(String.class), any(Duration.class)))
            .thenReturn(pictureLink);

        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenReturn(expertGroup);

        var param = new GetExpertGroupUseCase.Param(expertGroupId, currentUserId);
        GetExpertGroupUseCase.Result result = service.getExpertGroup(param);

        ArgumentCaptor<Long> expertGroupIdArgument = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupPort).loadExpertGroup(expertGroupIdArgument.capture());

        assertEquals(expertGroupId, expertGroupIdArgument.getValue());
        assertNotNull(result);
        assertNotNull(result.expertGroup());
        assertEquals(expertGroup.getTitle(), result.expertGroup().getTitle());
        assertEquals(expertGroup.getBio(), result.expertGroup().getBio());
        assertEquals(expertGroup.getAbout(), result.expertGroup().getAbout());
        assertEquals(pictureLink, result.pictureLink());
        assertEquals(expertGroup.getWebsite(), result.expertGroup().getWebsite());
        assertTrue(result.editable());
    }

    @Test
    void testGetExpertGroup_NullPicture_ValidResults() {
        UUID currentUserId = UUID.randomUUID();
        ExpertGroup expertGroup = createExpertGroup(null, currentUserId);
        long expertGroupId = expertGroup.getId();

        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenReturn(expertGroup);

        var param = new GetExpertGroupUseCase.Param(expertGroupId, currentUserId);
        var result = service.getExpertGroup(param);

        ArgumentCaptor<Long> loadPortParam = ArgumentCaptor.forClass(Long.class);

        verify(loadExpertGroupPort).loadExpertGroup(loadPortParam.capture());

        assertNotNull(result);
        assertNull(result.pictureLink());
        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetExpertGroup_ValidInputs_nonEditableGroup() {
        UUID currentUserId = UUID.randomUUID();
        ExpertGroup expertGroup = createExpertGroup(null, UUID.randomUUID());
        long expertGroupId = expertGroup.getId();

        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenReturn(expertGroup);

        var param = new GetExpertGroupUseCase.Param(expertGroupId, currentUserId);
        GetExpertGroupUseCase.Result result = service.getExpertGroup(param);

        ArgumentCaptor<Long> expertGroupIdArgument = ArgumentCaptor.forClass(Long.class);

        verify(loadExpertGroupPort).loadExpertGroup(expertGroupIdArgument.capture());

        assertEquals(expertGroupId, expertGroupIdArgument.getValue());
        assertNotNull(result);
        assertFalse(result.editable());
    }

    @Test
    void testGetExpertGroup_ValidInputs_expertGroupNotFound() {
        long expertGroupId = 123L;
        when(loadExpertGroupPort.loadExpertGroup(anyLong()))
            .thenThrow(new ResourceNotFoundException(GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND));

        var param = new GetExpertGroupUseCase.Param(expertGroupId, UUID.randomUUID());

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getExpertGroup(param));
        assertThat(throwable).hasMessage(GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND);

        verify(loadExpertGroupPort).loadExpertGroup(expertGroupId);
    }
}

