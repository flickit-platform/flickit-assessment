package org.flickit.assessment.kit.application.port.in.question;

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

class UpdateQuestionsOrderUseCaseParamTest {

    @Test
    void testUpdateQuestionsOrderUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_QUESTIONS_ORDER_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionsOrderUseCaseParam_ordersParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.orders(null)));
        assertThat(throwable).hasMessage("orders: " + UPDATE_QUESTIONS_ORDER_ORDERS_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(List.of(new UpdateQuestionsOrderUseCase.Param.QuestionOrder(2L, 5)))));
        AssertionsForClassTypes.assertThat(throwable).hasMessage("orders: " + UPDATE_QUESTIONS_ORDERS_ORDERS_SIZE_MIN);
    }

    @Test
    void testUpdateQuestionsOrderUseCaseParam_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createQuestionOrder(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + UPDATE_QUESTIONS_ORDER_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionsOrderUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createQuestionOrder(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_QUESTIONS_ORDER_INDEX_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createQuestionOrder(b -> b.index(0)));
        AssertionsForClassTypes.assertThat(throwable).hasMessage("index: " + UPDATE_QUESTIONS_ORDERS_INDEX_MIN);
    }

    @Test
    void testUpdateQuestionsOrderUseCaseParam_questionnaireIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + UPDATE_QUESTIONS_ORDER_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionsOrderUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateQuestionsOrderUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private UpdateQuestionsOrderUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionsOrderUseCase.Param.builder()
                .kitVersionId(1L)
                .orders(List.of(new UpdateQuestionsOrderUseCase.Param.QuestionOrder(1L, 1),
                        new UpdateQuestionsOrderUseCase.Param.QuestionOrder(2L, 2)))
                .questionnaireId(1L)
                .currentUserId(UUID.randomUUID());
    }

    private void createQuestionOrder(Consumer<UpdateQuestionsOrderUseCase.Param.QuestionOrder.QuestionOrderBuilder> changer) {
        var questionOrder = questionOrderBuilder();
        changer.accept(questionOrder);
        questionOrder.build();
    }

    private UpdateQuestionsOrderUseCase.Param.QuestionOrder.QuestionOrderBuilder questionOrderBuilder() {
        return UpdateQuestionsOrderUseCase.Param.QuestionOrder.builder()
                .questionId(1L)
                .index(1);
    }
}
