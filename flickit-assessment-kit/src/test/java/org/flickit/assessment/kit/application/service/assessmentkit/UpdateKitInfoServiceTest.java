package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Param;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Result;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.KitTagMother;
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

    @Mock
    private LoadAssessmentKitPort loadKitPort;

    @Mock
    private LoadKitTagsListPort loadKitTagsListPort;

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
        var param = new Param(kitId, newTitle, null, null, null, null, null, null, currentUserId);
        var result = new Result(newTitle, "summary", TRUE, FALSE, 0.0, "about", List.of(new KitTag(2L, "tag title")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newTitle, portParam.getValue().title());
        assertEquals(newTitle, serviceResult.title());
    }

    @Test
    void testUpdateKitInfo_EditSummary_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        String newSummary = "new summary";
        var param = new Param(kitId, null, newSummary, null, null, null, null, null, currentUserId);
        var result = new Result("title", newSummary, TRUE, FALSE, 0.0, "about", List.of(new KitTag(2L, "tag title")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newSummary, portParam.getValue().summary());
        assertEquals(newSummary, serviceResult.summary());
    }

    @Test
    void testUpdateKitInfo_EditPublished_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        Boolean newPublished = FALSE;
        var param = new Param(kitId, null, null, newPublished, null, null, null, null, currentUserId);
        var result = new Result("title", "summary", newPublished, FALSE, 0.0, "about", List.of(new KitTag(2L, "tag title")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newPublished, portParam.getValue().published());
        assertEquals(newPublished, serviceResult.published());
    }

    @Test
    void testUpdateKitInfo_EditIsPrivate_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        Boolean newIsPrivate = TRUE;
        var param = new Param(kitId, null, null, null, newIsPrivate, null, null, null, currentUserId);
        var result = new Result("title", "summary", TRUE, newIsPrivate, 0.0, "about", List.of(new KitTag(2L, "tag title")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newIsPrivate, portParam.getValue().isPrivate());
        assertEquals(newIsPrivate, serviceResult.isPrivate());
    }

    @Test
    void testUpdateKitInfo_EditPrice_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        Double newPrice = 2D;
        var param = new Param(kitId, null, null, null, null, newPrice, null, null, currentUserId);
        var result = new Result("title", "summary", TRUE, FALSE, 0.0, "about", List.of(new KitTag(2L, "tag title")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newPrice, portParam.getValue().price());
        assertEquals(0.0, serviceResult.price());
    }

    @Test
    void testUpdateKitInfo_EditAbout_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        String newAbout = "new about";
        var param = new Param(kitId, null, null, null, null, null, newAbout, null, currentUserId);
        var result = new Result("title", "summary", TRUE, FALSE, 0.0, newAbout, List.of(new KitTag(2L, "tag title")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertEquals(newAbout, portParam.getValue().about());
        assertEquals(newAbout, serviceResult.about());
    }

    @Test
    void testUpdateKitInfo_EditTags_ValidResults() {
        Long kitId = 1L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        var newUpdateKitInfoTag = new KitTag(3L, "new tag title");
        List<Long> newTags = List.of(3L);
        var param = new Param(kitId, null, null, null, null, null, null, newTags, currentUserId);
        var result = new Result("title", "summary", TRUE, FALSE, 0.0, "about", List.of(newUpdateKitInfoTag));

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(kitId, portParam.getValue().kitId());
        assertIterableEquals(newTags, portParam.getValue().tags());
        assertEquals(newTags.size(), serviceResult.tags().size());
        assertEquals(newTags.get(0), serviceResult.tags().get(0).getId());
    }

    @Test
    void testUpdateKitInfo_EditNothing_ValidResults() {
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = expertGroup.getOwnerId();
        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        List<KitTag> kitTags = List.of(
            KitTagMother.createKitTag("software"),
            KitTagMother.createKitTag("security")
        );
        var param = new Param(assessmentKit.getId(), null, null, null, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(assessmentKit.getId())).thenReturn(expertGroup);
        when(loadKitPort.load(assessmentKit.getId())).thenReturn(assessmentKit);
        when(loadKitTagsListPort.load(assessmentKit.getId())).thenReturn(kitTags);

        Result serviceResult = service.updateKitInfo(param);

        verify(updateKitInfoPort, never()).update(any());

        assertEquals(assessmentKit.getTitle(), serviceResult.title());
        assertEquals(assessmentKit.getSummary(), serviceResult.summary());
        assertEquals(assessmentKit.isPublished(), serviceResult.published());
        assertEquals(assessmentKit.isPrivate(), serviceResult.isPrivate());
        assertEquals(0.0, serviceResult.price());
        assertEquals(assessmentKit.getAbout(), serviceResult.about());
        assertEquals(kitTags.size(), serviceResult.tags().size());
    }
}
