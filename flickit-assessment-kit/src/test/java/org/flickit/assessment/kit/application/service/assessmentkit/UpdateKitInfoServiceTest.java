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

    private static final Long KIT_ID = 1L;
    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final Boolean PUBLISHED = Boolean.TRUE;
    private static final Boolean IS_PRIVATE = Boolean.FALSE;
    private static final Double PRICE = 0D;
    private static final String ABOUT = "about";
    private static final KitTag EDIT_KIT_INFO_TAG = new KitTag(2L, "tag title");
    private static final ExpertGroup EXPERT_GROUP = ExpertGroupMother.createExpertGroup();
    private static final UUID CURRENT_USER_ID = EXPERT_GROUP.getOwnerId();

    @Test
    void testUpdateKitInfo_KitNotFound_ErrorMessage() {
        var param = new Param(KIT_ID, TITLE, null, null, null, null, null, null, CURRENT_USER_ID);

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.updateKitInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testUpdateKitInfo_CurrentUserNotAllowed_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var param = new Param(KIT_ID, TITLE, null, null, null, null, null, null, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.updateKitInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testUpdateKitInfo_EditTitle_ValidResults() {
        String newTitle = "new title";
        var param = new Param(KIT_ID, newTitle, null, null, null, null, null, null, CURRENT_USER_ID);
        var result = new Result(newTitle, SUMMARY, PUBLISHED, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertEquals(newTitle, portParam.getValue().title());
        assertEquals(newTitle, serviceResult.title());
    }

    @Test
    void testUpdateKitInfo_EditSummary_ValidResults() {
        String newSummary = "new summary";
        var param = new Param(KIT_ID, null, newSummary, null, null, null, null, null, CURRENT_USER_ID);
        var result = new Result(TITLE, newSummary, PUBLISHED, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertEquals(newSummary, portParam.getValue().summary());
        assertEquals(newSummary, serviceResult.summary());
    }

    @Test
    void testUpdateKitInfo_EditPublished_ValidResults() {
        Boolean newPublished = !PUBLISHED;
        var param = new Param(KIT_ID, null, null, newPublished, null, null, null, null, CURRENT_USER_ID);
        var result = new Result(TITLE, SUMMARY, newPublished, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertEquals(newPublished, portParam.getValue().published());
        assertEquals(newPublished, serviceResult.published());
    }

    @Test
    void testUpdateKitInfo_EditIsPrivate_ValidResults() {
        Boolean newIsPrivate = !PUBLISHED;
        var param = new Param(KIT_ID, null, null, null, newIsPrivate, null, null, null, CURRENT_USER_ID);
        var result = new Result(TITLE, SUMMARY, PUBLISHED, newIsPrivate, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertEquals(newIsPrivate, portParam.getValue().isPrivate());
        assertEquals(newIsPrivate, serviceResult.isPrivate());
    }

    @Test
    void testUpdateKitInfo_EditPrice_ValidResults() {
        Double newPrice = 2D;
        var param = new Param(KIT_ID, null, null, null, null, newPrice, null, null, CURRENT_USER_ID);
        var result = new Result(TITLE, SUMMARY, PUBLISHED, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertEquals(newPrice, portParam.getValue().price());
        assertEquals(PRICE, serviceResult.price());
    }

    @Test
    void testUpdateKitInfo_EditAbout_ValidResults() {
        String newAbout = "new about";
        var param = new Param(KIT_ID, null, null, null, null, null, newAbout, null, CURRENT_USER_ID);
        var result = new Result(TITLE, SUMMARY, PUBLISHED, IS_PRIVATE, PRICE, newAbout, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertEquals(newAbout, portParam.getValue().about());
        assertEquals(newAbout, serviceResult.about());
    }

    @Test
    void testUpdateKitInfo_EditTags_ValidResults() {
        var newUpdateKitInfoTag = new KitTag(3L, "new tag title");
        List<Long> newTags = List.of(3L);
        var param = new Param(KIT_ID, null, null, null, null, null, null, newTags, CURRENT_USER_ID);
        var result = new Result(TITLE, SUMMARY, PUBLISHED, IS_PRIVATE, PRICE, ABOUT, List.of(newUpdateKitInfoTag));

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(EXPERT_GROUP);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        Result serviceResult = service.updateKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().kitId());
        assertIterableEquals(newTags, portParam.getValue().tags());
        assertEquals(newTags.size(), serviceResult.tags().size());
        assertEquals(newTags.get(0), serviceResult.tags().get(0).getId());
    }

    @Test
    void testUpdateKitInfo_EditNothing_ValidResults() {
        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        List<KitTag> kitTags = List.of(
            KitTagMother.createKitTag("software"),
            KitTagMother.createKitTag("security")
        );
        var param = new Param(assessmentKit.getId(), null, null, null, null, null, null, null, CURRENT_USER_ID);

        when(loadKitExpertGroupPort.loadKitExpertGroup(assessmentKit.getId())).thenReturn(EXPERT_GROUP);
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
