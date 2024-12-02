package org.flickit.assessment.kit.application.service.kitcustom;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.privateKit;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void testUpdateKitCustom_WhenKitIsPrivateAndCurrentUserHasNotAccess_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateKitCustomUseCase.Param.ParamBuilder::build);
        var kit = privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(kit.getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateKitCustom(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateKitCustom_WhenKitIsPrivateAndCurrentUserHasAccessToKit_ThenCreateKitCustom() {
        var param = createParam(UpdateKitCustomUseCase.Param.ParamBuilder::build);
        var kit = privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        doNothing().when(updateKitCustomPort).update(any(UpdateKitCustomPort.Param.class));

        service.updateKitCustom(param);

        assertUpdateKitCustomPortParamMapping(param);
    }

    @Test
    void testUpdateKitCustom_WhenKitIsPublic_ThenCreateKitCustom() {
        var param = createParam(UpdateKitCustomUseCase.Param.ParamBuilder::build);
        var kit = simpleKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        doNothing().when(updateKitCustomPort).update(any(UpdateKitCustomPort.Param.class));

        service.updateKitCustom(param);

        assertUpdateKitCustomPortParamMapping(param);

        verifyNoInteractions(checkKitUserAccessPort);
    }

    private void assertUpdateKitCustomPortParamMapping(UpdateKitCustomUseCase.Param param) {
        verify(updateKitCustomPort).update(portParamCaptor.capture());
        assertNotNull(portParamCaptor.getValue());
        assertEquals(param.getKitId(), portParamCaptor.getValue().kitId());
        assertEquals(param.getTitle(), portParamCaptor.getValue().title());
        assertEquals("title", portParamCaptor.getValue().code());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());

        var actualSubjects = portParamCaptor.getValue().customData().subjects();
        var expectedSubjects = param.getCustomData().customSubjects();
        Assertions.assertThat(actualSubjects)
            .zipSatisfy(expectedSubjects, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getWeight(), actual.weight());
            });

        var actualAttributes = portParamCaptor.getValue().customData().attributes();
        var expectedAttributes = param.getCustomData().customAttributes();
        Assertions.assertThat(actualAttributes)
            .zipSatisfy(expectedAttributes, (actual, expected) -> {
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
