package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
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
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditKitInfoServiceTest {

    @InjectMocks
    private EditKitInfoService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateKitInfoPort updateKitInfoPort;

    @Mock
    private LoadAssessmentKitPort loadKitPort;

    @Mock
    private LoadKitTagsListPort loadKitTagsListPort;

    private static final Long KIT_ID = 1L;
    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final Boolean IS_ACTIVE = Boolean.TRUE;
    private static final Boolean IS_PRIVATE = Boolean.FALSE;
    private static final Double PRICE = 0D;
    private static final String ABOUT = "about";
    private static final EditKitInfoUseCase.EditKitInfoTag EDIT_KIT_INFO_TAG = new EditKitInfoUseCase.EditKitInfoTag(2L, "tag title");
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testEditKitInfo_KitNotFound_ErrorMessage() {
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, null, null, null, null, null, null, CURRENT_USER_ID);

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.editKitInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testEditKitInfo_ExpertGroupNotFound_ErrorMessage() {
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, null, null, null, null, null, null, CURRENT_USER_ID);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.editKitInfo(param));
        assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);
    }

    @Test
    void testEditKitInfo_CurrentUserNotAllowed_ErrorMessage() {
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, null, null, null, null, null, null, CURRENT_USER_ID);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.editKitInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testEditKitInfo_EditTitle_ValidResults() {
        String newTitle = "new title";
        var param = new EditKitInfoUseCase.Param(KIT_ID, newTitle, null, null, null, null, null, null, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(newTitle, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertEquals(newTitle, portParam.getValue().title());
        assertEquals(newTitle, serviceResult.title());
    }

    @Test
    void testEditKitInfo_EditSummary_ValidResults() {
        String newSummary = "new summary";
        var param = new EditKitInfoUseCase.Param(KIT_ID, null, newSummary, null, null, null, null, null, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, newSummary, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertEquals(newSummary, portParam.getValue().summary());
        assertEquals(newSummary, serviceResult.summary());
    }

    @Test
    void testEditKitInfo_EditIsActive_ValidResults() {
        Boolean newIsActive = !IS_ACTIVE;
        var param = new EditKitInfoUseCase.Param(KIT_ID, null, null, newIsActive, null, null, null, null, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, newIsActive, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertEquals(newIsActive, portParam.getValue().isActive());
        assertEquals(newIsActive, serviceResult.isActive());
    }

    @Test
    void testEditKitInfo_EditIsPrivate_ValidResults() {
        Boolean newIsPrivate = !IS_ACTIVE;
        var param = new EditKitInfoUseCase.Param(KIT_ID, null, null, null, newIsPrivate, null, null, null, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, newIsPrivate, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertEquals(newIsPrivate, portParam.getValue().isPrivate());
        assertEquals(newIsPrivate, serviceResult.isPrivate());
    }

    @Test
    void testEditKitInfo_EditPrice_ValidResults() {
        Double newPrice = 2D;
        var param = new EditKitInfoUseCase.Param(KIT_ID, null, null, null, null, newPrice, null, null, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertEquals(newPrice, portParam.getValue().price());
        assertEquals(PRICE, serviceResult.price());
    }

    @Test
    void testEditKitInfo_EditAbout_ValidResults() {
        String newAbout = "new about";
        var param = new EditKitInfoUseCase.Param(KIT_ID, null, null, null, null, null, newAbout, null, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, newAbout, List.of(EDIT_KIT_INFO_TAG));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertEquals(newAbout, portParam.getValue().about());
        assertEquals(newAbout, serviceResult.about());
    }

    @Test
    void testEditKitInfo_EditTags_ValidResults() {
        var newEditKitInfoTag = new EditKitInfoUseCase.EditKitInfoTag(3L, "new tag title");
        List<Long> newTags = List.of(3L);
        var param = new EditKitInfoUseCase.Param(KIT_ID, null, null, null, null, null, null, newTags, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(newEditKitInfoTag));
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(KIT_ID)).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(updateKitInfoPort.update(any())).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(KIT_ID, portParam.getValue().assessmentKitId());
        assertIterableEquals(newTags, portParam.getValue().tags());
        assertEquals(newTags.size(), serviceResult.tags().size());
        assertEquals(newTags.get(0), serviceResult.tags().get(0).id());
    }

    @Test
    void testEditKitInfo_EditNothing_ValidResults() {
        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        List<KitTag> kitTags = List.of(
            KitTagMother.createKitTag("software"),
            KitTagMother.createKitTag("security")
        );
        var param = new EditKitInfoUseCase.Param(assessmentKit.getId(), null, null, null, null, null, null, null, CURRENT_USER_ID);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(assessmentKit.getId())).thenReturn(expertGroup);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroup.getId())).thenReturn(CURRENT_USER_ID);
        when(loadKitPort.load(assessmentKit.getId())).thenReturn(assessmentKit);
        when(loadKitTagsListPort.load(assessmentKit.getId())).thenReturn(kitTags);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        verify(updateKitInfoPort, never()).update(any());

        assertEquals(assessmentKit.getTitle(), serviceResult.title());
        assertEquals(assessmentKit.getSummary(), serviceResult.summary());
        assertEquals(assessmentKit.isPublished(), serviceResult.isActive());
        assertEquals(assessmentKit.isPrivate(), serviceResult.isPrivate());
        assertEquals(0.0, serviceResult.price());
        assertEquals(assessmentKit.getAbout(), serviceResult.about());
        assertEquals(kitTags.size(), serviceResult.tags().size());
    }

}
