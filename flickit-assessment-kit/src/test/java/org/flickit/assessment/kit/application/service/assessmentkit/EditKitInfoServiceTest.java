package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

    private static final Long KIT_ID = 1L;
    private static final Long EXPERT_GROUP_ID = 1L;
    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final Boolean IS_ACTIVE = Boolean.TRUE;
    private static final Boolean IS_PRIVATE = Boolean.FALSE;
    private static final Double PRICE = 0D;
    private static final String ABOUT = "about";
    private static final List<Long> TAGS = List.of(2L);
    private static final EditKitInfoUseCase.EditKitInfoTag EDIT_KIT_INFO_TAG = new EditKitInfoUseCase.EditKitInfoTag(2L, "tag title");
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testEditKitInfo_KitNotFound_ErrorMessage() {
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID);

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.editKitInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testEditKitInfo_ExpertGroupNotFound_ErrorMessage() {
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID);

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.editKitInfo(param));
        assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);
    }

    @Test
    void testEditKitInfo_CurrentUserNotAllowed_ErrorMessage() {
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID);

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(UUID.randomUUID()));

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.editKitInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testEditKitInfo_EditTitle_ValidResults() {
        String newTitle = "new title";
        var param = new EditKitInfoUseCase.Param(KIT_ID, newTitle, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(newTitle, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(newTitle, serviceResult.title());

        assertEquals(SUMMARY, serviceResult.summary());
        assertEquals(IS_ACTIVE, serviceResult.isActive());
        assertEquals(IS_PRIVATE, serviceResult.isPrivate());
        assertEquals(PRICE, serviceResult.price());
        assertEquals(ABOUT, serviceResult.about());
        assertEquals(TAGS.size(), serviceResult.tags().size());
    }

    @Test
    void testEditKitInfo_EditSummary_ValidResults() {
        String newSummary = "new summary";
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, newSummary, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, newSummary, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(newSummary, serviceResult.summary());

        assertEquals(TITLE, serviceResult.title());
        assertEquals(IS_ACTIVE, serviceResult.isActive());
        assertEquals(IS_PRIVATE, serviceResult.isPrivate());
        assertEquals(PRICE, serviceResult.price());
        assertEquals(ABOUT, serviceResult.about());
        assertEquals(TAGS.size(), serviceResult.tags().size());
    }

    @Test
    void testEditKitInfo_EditIsActive_ValidResults() {
        Boolean newIsActive = !IS_ACTIVE;
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, newIsActive, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, newIsActive, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(newIsActive, serviceResult.isActive());

        assertEquals(TITLE, serviceResult.title());
        assertEquals(SUMMARY, serviceResult.summary());
        assertEquals(IS_PRIVATE, serviceResult.isPrivate());
        assertEquals(PRICE, serviceResult.price());
        assertEquals(ABOUT, serviceResult.about());
        assertEquals(TAGS.size(), serviceResult.tags().size());
    }

    @Test
    void testEditKitInfo_EditIsPrivate_ValidResults() {
        Boolean newIsPrivate = !IS_ACTIVE;
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, newIsPrivate, PRICE, ABOUT, TAGS, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, newIsPrivate, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(newIsPrivate, serviceResult.isPrivate());

        assertEquals(TITLE, serviceResult.title());
        assertEquals(SUMMARY, serviceResult.summary());
        assertEquals(IS_ACTIVE, serviceResult.isActive());
        assertEquals(PRICE, serviceResult.price());
        assertEquals(ABOUT, serviceResult.about());
        assertEquals(TAGS.size(), serviceResult.tags().size());
    }

    @Test
    void testEditKitInfo_EditPrice_ValidResults() {
        Double newPrice = 2D;
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, newPrice, ABOUT, TAGS, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(PRICE, serviceResult.price());

        assertEquals(TITLE, serviceResult.title());
        assertEquals(SUMMARY, serviceResult.summary());
        assertEquals(IS_ACTIVE, serviceResult.isActive());
        assertEquals(IS_PRIVATE, serviceResult.isPrivate());
        assertEquals(ABOUT, serviceResult.about());
        assertEquals(TAGS.size(), serviceResult.tags().size());
    }

    @Test
    void testEditKitInfo_EditAbout_ValidResults() {
        String newAbout = "new about";
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, newAbout, TAGS, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, newAbout, List.of(EDIT_KIT_INFO_TAG));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(newAbout, serviceResult.about());

        assertEquals(TITLE, serviceResult.title());
        assertEquals(SUMMARY, serviceResult.summary());
        assertEquals(IS_ACTIVE, serviceResult.isActive());
        assertEquals(IS_PRIVATE, serviceResult.isPrivate());
        assertEquals(PRICE, serviceResult.price());
        assertEquals(TAGS.size(), serviceResult.tags().size());
    }

    @Test
    void testEditKitInfo_EditTags_ValidResults() {
        var newEditKitInfoTag = new EditKitInfoUseCase.EditKitInfoTag(3L, "new tag title");
        List<Long> newTags = List.of(3L);
        var param = new EditKitInfoUseCase.Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, newTags, CURRENT_USER_ID);
        var result = new EditKitInfoUseCase.Result(TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, List.of(newEditKitInfoTag));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(KIT_ID)).thenReturn(EXPERT_GROUP_ID);
        when(loadExpertGroupOwnerPort.loadOwnerId(EXPERT_GROUP_ID)).thenReturn(Optional.of(CURRENT_USER_ID));
        when(updateKitInfoPort.update(param)).thenReturn(result);

        EditKitInfoUseCase.Result serviceResult = service.editKitInfo(param);

        assertEquals(newTags.size(), serviceResult.tags().size());
        assertEquals(newTags.get(0), serviceResult.tags().get(0).id());

        assertEquals(TITLE, serviceResult.title());
        assertEquals(SUMMARY, serviceResult.summary());
        assertEquals(IS_ACTIVE, serviceResult.isActive());
        assertEquals(IS_PRIVATE, serviceResult.isPrivate());
        assertEquals(PRICE, serviceResult.price());
        assertEquals(ABOUT, serviceResult.about());
    }

}
