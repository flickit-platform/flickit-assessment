package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_MEASURE_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_MEASURE_MEASURE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetKitMeasureDetailUseCaseParamTest {

    @Test
    void testGetKitMeasureDetailUseCaseParam_kitIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_MEASURE_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitMeasureDetailUseCaseParam_measureIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.measureId(null)));
        assertThat(throwable).hasMessage("measureId: " + GET_KIT_MEASURE_MEASURE_ID_NOT_NULL);
    }

    @Test
    void testGetKitMeasureDetailUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetKitMeasureDetailUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetKitMeasureDetailUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitMeasureDetailUseCase.Param.builder()
            .kitId(1L)
            .measureId(10L)
            .currentUserId(UUID.randomUUID());
    }
}
