package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateMaturityLevelOrdersUseCaseParamTest {

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_MATURITY_LEVEL_ORDERS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_ordersParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(null)));
        assertThat(throwable).hasMessage("orders: " + UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(List.of(new UpdateMaturityLevelOrdersUseCase.MaturityLevelParam(123L, 3)))));
        assertThat(throwable).hasMessage("orders: " + UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_SIZE_MIN);
    }

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_maturityLevelIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParamMaturityLevelOrder(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + UPDATE_MATURITY_LEVEL_ORDERS_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_maturityLevelIndexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParamMaturityLevelOrder(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_MATURITY_LEVEL_ORDERS_MATURITY_LEVEL_INDEX_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParamMaturityLevelOrder(b -> b.index(0)));
        assertThat(throwable).hasMessage("index: " + UPDATE_MATURITY_LEVEL_ORDERS_MATURITY_LEVEL_INDEX_MIN);
    }

    private void createParamMaturityLevelOrder(Consumer<UpdateMaturityLevelOrdersUseCase.MaturityLevelParam.MaturityLevelParamBuilder> changer) {
        var paramBuilder = maturityLevelparamBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateMaturityLevelOrdersUseCase.MaturityLevelParam.MaturityLevelParamBuilder maturityLevelparamBuilder() {
        return UpdateMaturityLevelOrdersUseCase.MaturityLevelParam.builder()
            .id(1L)
            .index(2);
    }

    private void createParam(Consumer<UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMaturityLevelOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new UpdateMaturityLevelOrdersUseCase.MaturityLevelParam(123L, 3),
                new UpdateMaturityLevelOrdersUseCase.MaturityLevelParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
