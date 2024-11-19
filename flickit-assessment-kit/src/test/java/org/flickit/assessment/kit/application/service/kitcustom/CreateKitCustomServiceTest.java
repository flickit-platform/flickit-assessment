package org.flickit.assessment.kit.application.service.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.in.kitcustom.CreateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateKitCustomServiceTest {

    @InjectMocks
    private CreateKitCustomService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private CreateKitCustomPort createKitCustomPort;

    @Captor
    private ArgumentCaptor<CreateKitCustomPort.Param> portParamCaptor;

    @Mock
    private ObjectMapper mapper;

    @Test
    @SneakyThrows
    void testCreateKitCustom_WhenKitIsPublic_ThenCreateKitCustom() {
        var param = createParam(CreateKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        long kitCustomId = 1;
        String kitCustomData = """
            {"subs":[{"id":1000,"w":1}],"atts":[{"id":200,"w":2}]}
            """;

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(createKitCustomPort.persist(any(CreateKitCustomPort.Param.class))).thenReturn(kitCustomId);
        when(mapper.writeValueAsString(any(KitCustomData.class))).thenReturn(kitCustomData);

        long actualKitCustomId = service.createKitCustom(param);
        assertEquals(kitCustomId, actualKitCustomId);

        assertCreateKitCustomPortParamMapping(param, kitCustomData);

        assertKitCustomDataMapping(param.getCustomData());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testCreateKitCustom_WhenKitIsPrivateAndCurrentUserHasNoAccessToKit_ThenThrowAccessDeniedException() {
        var param = createParam(CreateKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.privateKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(false);

        var accessDeniedException = assertThrows(AccessDeniedException.class, () -> service.createKitCustom(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, accessDeniedException.getMessage());

        verifyNoInteractions(createKitCustomPort, mapper);
    }

    @Test
    @SneakyThrows
    void testCreateKitCustom_WhenKitIsPrivateAndCurrentUserHasAccessToKit_ThenCreateKitCustom() {
        var param = createParam(CreateKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = AssessmentKitMother.privateKit();
        long kitCustomId = 1;
        String kitCustomData = """
            {"subs":[{"id":1000,"w":1}],"atts":[{"id":200,"w":2}]}
            """;

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        when(createKitCustomPort.persist(any(CreateKitCustomPort.Param.class))).thenReturn(kitCustomId);
        when(mapper.writeValueAsString(any(KitCustomData.class))).thenReturn(kitCustomData);

        long actualKitCustomId = service.createKitCustom(param);
        assertEquals(kitCustomId, actualKitCustomId);

        assertCreateKitCustomPortParamMapping(param, kitCustomData);

        assertKitCustomDataMapping(param.getCustomData());
    }

    private void assertCreateKitCustomPortParamMapping(CreateKitCustomUseCase.Param param, String kitCustomData) {
        verify(createKitCustomPort).persist(portParamCaptor.capture());
        assertNotNull(portParamCaptor.getValue());
        assertEquals(param.getKitId(), portParamCaptor.getValue().kitId());
        assertEquals(param.getTitle(), portParamCaptor.getValue().title());
        assertEquals("custom-title", portParamCaptor.getValue().code());
        assertEquals(kitCustomData, portParamCaptor.getValue().customData());
        assertNotNull(portParamCaptor.getValue().creationTime());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().createdBy());
    }

    @SneakyThrows
    private void assertKitCustomDataMapping(CreateKitCustomUseCase.Param.KitCustomData kitCustomData) {
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

    private CreateKitCustomUseCase.Param createParam(Consumer<CreateKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CreateKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        var customSubject = new CreateKitCustomUseCase.Param.KitCustomData.CustomSubject(111L, 1);
        var customAttribute = new CreateKitCustomUseCase.Param.KitCustomData.CustomAttribute(123L, 5);
        var customData = new CreateKitCustomUseCase.Param.KitCustomData(List.of(customSubject), List.of(customAttribute));
        return CreateKitCustomUseCase.Param.builder()
            .kitId(1L)
            .title("custom title")
            .customData(customData)
            .currentUserId(UUID.randomUUID());
    }
}
