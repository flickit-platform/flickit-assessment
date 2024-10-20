package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitInfoServiceTest {

    @InjectMocks
    private UpdateKitInfoService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private UpdateKitInfoPort updateKitInfoPort;

    @Test
    void testUpdateKitInfo_KitNotFound_ErrorMessage() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        var param = new Param(kitId,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.updateKitInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testUpdateKitInfo_CurrentUserNotAllowed_ErrorMessage() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = UUID.randomUUID();
        var param = new Param(kitId, null, null, null, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.updateKitInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testUpdateKitInfo_EditTitle_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        String newTitle = "new title";
        String newCode = "new-title";
        var param = new Param(kitId, newTitle, null, null, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newTitle, portParam.getValue().title());
        assertEquals(newCode, portParam.getValue().code());
    }

    @Test
    void testUpdateKitInfo_EditSummary_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        String newSummary = "new summary";
        var param = new Param(kitId, null, newSummary, null, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newSummary, portParam.getValue().summary());
    }

    @Test
    void testUpdateKitInfo_EditPublished_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        Boolean newPublished = FALSE;
        var param = new Param(kitId, null, null, newPublished, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newPublished, portParam.getValue().published());
    }

    @Test
    void testUpdateKitInfo_EditIsPrivate_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        Boolean newIsPrivate = TRUE;
        var param = new Param(kitId, null, null, null, newIsPrivate, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newIsPrivate, portParam.getValue().isPrivate());
    }

    @Test
    void testUpdateKitInfo_EditPrice_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        Double newPrice = 2D;
        var param = new Param(kitId, null, null, null, null, newPrice, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newPrice, portParam.getValue().price());
    }

    @Test
    void testUpdateKitInfo_EditAbout_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        String newAbout = "new about";
        var param = new Param(kitId, null, null, null, null, null, newAbout, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newAbout, portParam.getValue().about());
    }

    @Test
    void testUpdateKitInfo_EditTags_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        List<Long> newTags = List.of(3L);
        var param = new Param(kitId, null, null, null, null, null, null, newTags, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        doNothing().when(updateKitInfoPort).update(any());

        service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertIterableEquals(newTags, portParam.getValue().tags());
    }

    @Test
    void testUpdateKitInfo_EditNothing_ValidResults() {
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        var param = new Param(assessmentKit.getId(), null, null, null, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(assessmentKit.getId())).thenReturn(expertGroup);

        service.updateKitInfo(param);

        verify(updateKitInfoPort, never()).update(any());
    }
}
