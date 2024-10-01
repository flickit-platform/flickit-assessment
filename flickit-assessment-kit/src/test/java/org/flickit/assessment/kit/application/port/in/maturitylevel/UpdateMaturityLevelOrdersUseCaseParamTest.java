package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_MATURITY_LEVEL_ORDERS_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateMaturityLevelOrdersUseCaseParamTest {

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_MATURITY_LEVEL_ORDERS_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_ordersIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(null)));
        assertThat(throwable).hasMessage("orders: " + UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersUseCaseParam_currentUserIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateMaturityLevelOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMaturityLevelOrdersUseCase.Param.builder()
            .kitId(1L)
            .orders(List.of())
            .currentUserId(UUID.randomUUID());
    }
}
