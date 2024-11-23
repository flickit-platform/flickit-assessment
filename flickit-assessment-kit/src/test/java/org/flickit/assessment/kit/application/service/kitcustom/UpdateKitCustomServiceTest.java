package org.flickit.assessment.kit.application.service.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class UpdateKitCustomServiceTest {

    @InjectMocks
    private UpdateKitCustomService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private UpdateKitCustomPort updateKitCustomPort;

    @Captor
    private ArgumentCaptor<UpdateKitCustomPort.Param> portParamCaptor;

    @Mock
    private ObjectMapper mapper;

    private final UpdateKitCustomUseCase.Param param = createParam(UpdateKitCustomUseCase.Param.ParamBuilder::build);
    private final String kitCustomData = """
        {"subs":[{"id":1000,"w":1}],"atts":[{"id":200,"w":2}]}
        """;

    @Test
    @SneakyThrows
    void testUpdateKitCustom_WhenKitIsPrivateAndUserHasNotAccess_ThenThrowAccessDeniedException() {
        AssessmentKit kit = AssessmentKitMother.privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(kit.getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateKitCustom(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    @SneakyThrows
    void testUpdateKitCustom_WhenKitIsPrivateAndCurrentUserHasAccessToKit_ThenCreateKitCustom() {
        AssessmentKit kit = AssessmentKitMother.privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        doNothing().when(updateKitCustomPort).update(any(UpdateKitCustomPort.Param.class));
        when(mapper.writeValueAsString(any(KitCustomData.class))).thenReturn(kitCustomData);

        service.updateKitCustom(param);

        assertUpdateKitCustomPortParamMapping(param);
        assertKitCustomDataMapping(param.getCustomData());
    }

    @Test
    @SneakyThrows
    void testUpdateKitCustom_WhenKitIsPublic_ThenCreateKitCustom() {
        AssessmentKit kit = AssessmentKitMother.simpleKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        doNothing().when(updateKitCustomPort).update(any(UpdateKitCustomPort.Param.class));
        when(mapper.writeValueAsString(any(KitCustomData.class))).thenReturn(kitCustomData);

        service.updateKitCustom(param);

        assertUpdateKitCustomPortParamMapping(param);
        assertKitCustomDataMapping(param.getCustomData());
        verifyNoInteractions(checkKitUserAccessPort);
    }

    private void assertUpdateKitCustomPortParamMapping(UpdateKitCustomUseCase.Param param) {
        verify(updateKitCustomPort).update(portParamCaptor.capture());
        assertNotNull(portParamCaptor.getValue());
        assertEquals(param.getKitId(), portParamCaptor.getValue().kitId());
        assertEquals(param.getTitle(), portParamCaptor.getValue().title());
        assertEquals("title", portParamCaptor.getValue().code());
        assertEquals(kitCustomData, portParamCaptor.getValue().customData());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());
    }

    @SneakyThrows
    private void assertKitCustomDataMapping(UpdateKitCustomUseCase.Param.KitCustomData kitCustomData) {
        ArgumentCaptor<KitCustomData> customDataCaptor = ArgumentCaptor.forClass(KitCustomData.class);
        verify(mapper).writeValueAsString(customDataCaptor.capture());

        var subjects = customDataCaptor.getValue().subjects();
        var customSubjects = kitCustomData.customSubjects();
        Assertions.assertThat(subjects)
            .zipSatisfy(customSubjects, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getWeight(), actual.weight());
            });

        var attributes = customDataCaptor.getValue().attributes();
        var customAttributes = kitCustomData.customAttributes();
        Assertions.assertThat(attributes)
            .zipSatisfy(customAttributes, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getWeight(), actual.weight());
            });
    }

    private UpdateKitCustomUseCase.Param createParam(Consumer<UpdateKitCustomUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateKitCustomUseCase.Param.builder()
            .kitCustomId(12L)
            .kitId(1L)
            .title("title")
            .customData(createCustomDataParam(UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder::build))
            .currentUserId(UUID.randomUUID());
    }

    private UpdateKitCustomUseCase.Param.KitCustomData createCustomDataParam(Consumer<UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder> changer) {
        var paramBuilder = KitCustomDataBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder KitCustomDataBuilder() {
        var customSubject = new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(1L, 1);
        var customAttribute = new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(1L, 1);
        return UpdateKitCustomUseCase.Param.KitCustomData.builder()
            .customSubjects(List.of(customSubject))
            .customAttributes(List.of(customAttribute));
    }
}
