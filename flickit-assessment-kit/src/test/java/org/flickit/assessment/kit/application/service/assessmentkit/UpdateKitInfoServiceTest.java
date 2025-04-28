package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.SpringUtil;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitMetadata;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateKitInfoServiceTest {

    @InjectMocks
    private UpdateKitInfoService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private UpdateKitInfoPort updateKitInfoPort;

    @Mock
    private ApplicationContext applicationContext;

    @Captor
    ArgumentCaptor<UpdateKitInfoPort.Param> portParam = ArgumentCaptor.forClass(UpdateKitInfoPort.Param.class);

    private final ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
    private AssessmentKit kit = AssessmentKitMother.simpleKit();
    private UpdateKitInfoUseCase.Param param = createParam(UpdateKitInfoUseCase.Param.ParamBuilder::build);

    @Test
    void testUpdateKitInfo_whenKitNotExists_thenThrowResourceNotFoundError() {
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.updateKitInfo(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);

        verifyNoInteractions(loadAssessmentKitPort, updateKitInfoPort);
    }

    @Test
    void testUpdateKitInfo_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedError() {
        param = createParam(b -> b.currentUserId(UUID.randomUUID()));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.updateKitInfo(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(loadAssessmentKitPort, updateKitInfoPort);
    }

    @Test
    void testUpdateKitInfo_whenEditTitle_thenSuccessfulUpdate() {
        param = createParam(b -> b.title("new title").removeTranslations(true).translations(null));
        String newCode = generateSlugCode(param.getTitle());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getTitle(), portParam.getValue().title());
        assertEquals(newCode, portParam.getValue().code());
    }

    @Test
    void testUpdateKitInfo_whenEditSummary_thenSuccessfulUpdate() {
        param = createParam(b -> b.summary("new summary"));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);

        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getSummary(), portParam.getValue().summary());
    }

    @Test
    void testUpdateKitInfo_whenEditPublishedField_thenSuccessfulUpdate() {
        param = createParam(b -> b.published(false));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getPublished(), portParam.getValue().published());
    }

    @Test
    void testUpdateKitInfo_whenEditIsPrivateField_thenSuccessfulUpdate() {
        param = createParam(b -> b.isPrivate(true));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getIsPrivate(), portParam.getValue().isPrivate());
    }

    @Test
    void testUpdateKitInfo_whenEditPriceFieldAndMetadataField_thenSuccessfulUpdate() {
        var metadata = new KitMetadata("goal", null);
        param = createParam(b -> b.price(2d).metadata(metadata));
        kit = AssessmentKitMother.kitWithMetadata(metadata);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getPrice(), portParam.getValue().price());
    }

    @Test
    void testUpdateKitInfo_whenEditAboutField_thenSuccessfulUpdate() {
        var metadata = new KitMetadata(null, "context");
        param = createParam(b -> b.about("new about").metadata(metadata));
        kit = AssessmentKitMother.kitWithMetadata(metadata);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getAbout(), portParam.getValue().about());
    }

    @Test
    void testUpdateKitInfo_whenEditLangField_thenSuccessfulUpdate() {
        var props = new AppSpecProperties();
        doReturn(props).when(applicationContext).getBean(AppSpecProperties.class);
        new SpringUtil(applicationContext);

        param = createParam(b -> b.lang("FA"));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(KitLanguage.valueOf(param.getLang()), portParam.getValue().lang());
        assertEquals(param.getTranslations(), portParam.getValue().translations());
    }

    @Test
    void testUpdateKitInfo_whenEditTranslationsField_thenSuccessfulUpdate() {
        param = createParam(b -> b.translations(
            Map.of("EN", new KitTranslation("translated title", "translated summary", "translated about",
                new KitTranslation.MetadataTranslation("translated goal", "translated context")))));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getTranslations(), portParam.getValue().translations());
    }

    @Test
    void testUpdateKitInfo_whenRemoveTranslations_thenSuccessfulUpdate() {
        param = createParam(b -> b.removeTranslations(true));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertTrue(portParam.getValue().isRemoveTranslations());
    }

    @Test
    void testUpdateKitInfo_whenEditMetadataField_thenSuccessfulUpdate() {
        param = createParam(b -> b.metadata(new KitMetadata("translated goal", "translated context")));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertEquals(param.getMetadata(), portParam.getValue().metadata());
    }

    @Test
    void testUpdateKitInfo_whenRemoveMetadata_thenSuccessfulUpdate() {
        var metadata = new KitMetadata(null, null);
        param = createParam(b -> b.removeMetadata(true).metadata(metadata));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertTrue(portParam.getValue().isRemoveMetadata());
    }

    @Test
    void testUpdateKitInfo_whenEditTagsFiled_thenSuccessfulUpdate() {
        param = createParam(b -> b.tags(List.of(3L)));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verify(updateKitInfoPort, times(1)).update(portParam.capture());

        assertEquals(param.getKitId(), portParam.getValue().kitId());
        assertIterableEquals(param.getTags(), portParam.getValue().tags());
        assertEquals(param.getTranslations(), portParam.getValue().translations());
    }

    @Test
    void testUpdateKitInfo_whenEditNothing_thenUpdateNothing() {
        param = createParam(b -> b.kitId(kit.getId()));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);

        service.updateKitInfo(param);
        verifyNoInteractions(updateKitInfoPort);
    }

    private UpdateKitInfoUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateKitInfoUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateKitInfoUseCase.Param.builder()
            .kitId(1L)
            .currentUserId(expertGroup.getOwnerId());
    }
}
