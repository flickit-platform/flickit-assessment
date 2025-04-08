package org.flickit.assessment.kit.application.service.measure;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMeasureServiceTest {

    @InjectMocks
    private UpdateMeasureService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateMeasurePort updateMeasurePort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());
    private final UpdateMeasureUseCase.Param param = createParam(UpdateMeasureUseCase.Param.ParamBuilder::build);

    @Test
    void testUpdateMeasure_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId()))
            .thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateMeasure(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateMeasurePort);
    }

    @Test
    void testUpdateMeasure_whenCurrentUserIsExpertGroupOwner_thenUpdateMeasureSuccessfully() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId()))
            .thenReturn(param.getCurrentUserId());

        service.updateMeasure(param);

        var portParamCaptor = ArgumentCaptor.forClass(UpdateMeasurePort.Param.class);
        verify(updateMeasurePort).update(portParamCaptor.capture());

        assertEquals(param.getMeasureId(), portParamCaptor.getValue().id());
        assertEquals(kitVersion.getId(), portParamCaptor.getValue().kitVersionId());
        assertEquals(param.getTitle(), portParamCaptor.getValue().title());
        assertEquals(generateCode(param.getTitle()), portParamCaptor.getValue().code());
        assertEquals(param.getIndex(), portParamCaptor.getValue().index());
        assertEquals(param.getDescription(), portParamCaptor.getValue().description());
        assertNotNull(portParamCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), portParamCaptor.getValue().lastModifiedBy());
    }

    private UpdateMeasureUseCase.Param createParam(Consumer<UpdateMeasureUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateMeasureUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMeasureUseCase.Param.builder()
            .kitVersionId(kitVersion.getId())
            .measureId(1L)
            .title("abc")
            .index(1)
            .description("description")
            .currentUserId(UUID.randomUUID());
    }
}
