package org.flickit.assessment.kit.application.port.in.answeroptions;


import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAnswerOptionOrdersUseCaseParamTest {

    @Test
    void testUpdateAnswerOptionOrdersUseCaseParam_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ANSWER_OPTION_ORDERS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionOrdersUseCaseParam_ordersParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(null)));
        assertThat(throwable).hasMessage("orders: " + UPDATE_ANSWER_OPTION_ORDERS_ORDERS_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(List.of())));
        assertThat(throwable).hasMessage("orders: " + UPDATE_ANSWER_OPTION_ORDERS_ORDERS_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionOrdersUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionOrdersUseCaseParam_answerOptionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParamAnswerOptionOrders(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + UPDATE_ANSWER_OPTION_ORDERS_ANSWER_OPTION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionOrdersUseCaseParam_answerOptionIndexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParamAnswerOptionOrders(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_ANSWER_OPTION_ORDERS_ANSWER_OPTION_INDEX_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParamAnswerOptionOrders(b -> b.index(0)));
        assertThat(throwable).hasMessage("index: " + UPDATE_ANSWER_OPTION_ORDERS_ANSWER_OPTION_INDEX_MIN);
    }

    private void createParamAnswerOptionOrders(Consumer<UpdateAnswerOptionOrdersUseCase.AnswerOptionParam.AnswerOptionParamBuilder> changer) {
        var paramBuilder = answerOptionParamBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAnswerOptionOrdersUseCase.AnswerOptionParam.AnswerOptionParamBuilder answerOptionParamBuilder() {
        return UpdateAnswerOptionOrdersUseCase.AnswerOptionParam.builder()
            .id(1L)
            .index(2);
    }

    private void createParam(Consumer<UpdateAnswerOptionOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAnswerOptionOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerOptionOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new UpdateAnswerOptionOrdersUseCase.AnswerOptionParam(123L, 3),
                new UpdateAnswerOptionOrdersUseCase.AnswerOptionParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
