package org.flickit.assessment.kit.application.service.measure;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMeasureOrdersServiceTest {

    @InjectMocks
    private UpdateMeasureOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateMeasurePort updateMeasurePort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());
    UpdateMeasureOrdersUseCase.Param param = createParam(UpdateMeasureOrdersUseCase.Param.ParamBuilder::build);

    @Test
    void testUpdateMeasureOrders_whenUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.changeOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateMeasurePort);
    }

    @Test
    void testUpdateMeasureOrders_whenParametersAreValid_thenUpdatesSuccessfully() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());
        var updatePortParamCaptor = ArgumentCaptor.forClass(UpdateMeasurePort.UpdateOrderParam.class);

        service.changeOrders(param);
        verify(updateMeasurePort).updateOrders(updatePortParamCaptor.capture());

        assertEquals(param.getKitVersionId(), updatePortParamCaptor.getValue().kitVersionId());
        assertEquals(param.getCurrentUserId(), updatePortParamCaptor.getValue().lastModifiedBy());
        assertNotNull(updatePortParamCaptor.getValue().lastModificationTime());
        assertNotNull(updatePortParamCaptor.getValue().orders());
        assertThat(updatePortParamCaptor.getValue().orders())
            .zipSatisfy(param.getOrders(), (actual, expected) -> {
                assertEquals(expected.getId(), actual.measureId());
                assertEquals(expected.getIndex(), actual.index());
            });
    }

    private UpdateMeasureOrdersUseCase.Param createParam(Consumer<UpdateMeasureOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
        return paramBuilder.build();
    }

    private UpdateMeasureOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMeasureOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new UpdateMeasureOrdersUseCase.MeasureParam(123L, 3),
                new UpdateMeasureOrdersUseCase.MeasureParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
