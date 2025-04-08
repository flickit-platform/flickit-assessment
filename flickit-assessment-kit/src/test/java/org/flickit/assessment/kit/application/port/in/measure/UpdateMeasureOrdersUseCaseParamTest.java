package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateMeasureOrdersUseCaseParamTest {

    @Test
    void testUpdateMeasureOrdersUseCaseParam_kitVersionIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_MEASURE_ORDERS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateMeasureOrdersUseCaseParam_ordersParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(null)));
        assertThat(throwable).hasMessage("orders: " + UPDATE_MEASURE_ORDERS_ORDERS_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(List.of(new UpdateMeasureOrdersUseCase.MeasureParam(2L, 5)))));
        AssertionsForClassTypes.assertThat(throwable).hasMessage("orders: " + UPDATE_MEASURE_ORDERS_ORDERS_SIZE_MIN);
    }

    @Test
    void testUpdateMeasureOrdersUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersMaturityLevelUseCaseParam_idViolatesConstraints_ErrorMessage() {
        var throwableNullViolation = assertThrows(ConstraintViolationException.class,
            () -> createMeasureOrderParam(b -> b.id(null)));
        assertThat(throwableNullViolation).hasMessage("id: " + UPDATE_MEASURE_ORDERS_MEASURE_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersMaturityLevelUseCaseParam_indexViolatesConstraints_ErrorMessage() {
        var throwableNullViolation = assertThrows(ConstraintViolationException.class,
            () -> createMeasureOrderParam(b -> b.index(null)));
        assertThat(throwableNullViolation).hasMessage("index: " + UPDATE_MEASURE_ORDERS_MEASURE_INDEX_NOT_NULL);

        var throwableMinViolation = assertThrows(ConstraintViolationException.class,
            () -> createMeasureOrderParam(b -> b.index(0)));
        assertThat(throwableMinViolation).hasMessage("index: " + UPDATE_MEASURE_ORDERS_MEASURE_INDEX_MIN);
    }

    private void createMeasureOrderParam(Consumer<UpdateMeasureOrdersUseCase.MeasureParam.MeasureParamBuilder> changer) {
        var paramBuilder = measureParamBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateMeasureOrdersUseCase.MeasureParam.MeasureParamBuilder measureParamBuilder() {
        return UpdateMeasureOrdersUseCase.MeasureParam.builder()
            .id(1L)
            .index(2);
    }

    private void createParam(Consumer<UpdateMeasureOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
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
