package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQuestionnaireOrderUseCaseTest {

    @Test
    void testUpdateQuestionnaireOrdersUseCaseParam_kitVersionIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_QUESTIONNAIRE_ORDERS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireOrdersUseCaseParam_ordersParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.orders(null)));
        assertThat(throwable).hasMessage("orders: " + UPDATE_QUESTIONNAIRE_ORDERS_ORDERS_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireOrdersUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersMaturityLevelUseCaseParam_idViolatesConstraints_ErrorMessage() {
        var throwableNullViolation = assertThrows(ConstraintViolationException.class,
            () -> createQuestionnaireOrderParam(b -> b.id(null)));
        assertThat(throwableNullViolation).hasMessage("id: " + UPDATE_QUESTIONNAIRE_ORDERS_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelOrdersMaturityLevelUseCaseParam_indexViolatesConstraints_ErrorMessage() {
        var throwableNullViolation = assertThrows(ConstraintViolationException.class,
            () -> createQuestionnaireOrderParam(b -> b.index(null)));
        assertThat(throwableNullViolation).hasMessage("index: " + UPDATE_QUESTIONNAIRE_ORDERS_QUESTIONNAIRE_INDEX_NOT_NULL);

        var throwableMinViolation = assertThrows(ConstraintViolationException.class,
            () -> createQuestionnaireOrderParam(b -> b.index(0)));
        assertThat(throwableMinViolation).hasMessage("index: " + UPDATE_QUESTIONNAIRE_ORDERS_QUESTIONNAIRE_INDEX_MIN);
    }

    private void createQuestionnaireOrderParam(Consumer<UpdateQuestionnaireOrdersUseCase.QuestionnaireParam.QuestionnaireParamBuilder> changer) {
        var paramBuilder = questionnaireParamBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateQuestionnaireOrdersUseCase.QuestionnaireParam.QuestionnaireParamBuilder questionnaireParamBuilder() {
        return UpdateQuestionnaireOrdersUseCase.QuestionnaireParam.builder()
            .id(1L)
            .index(2);
    }

    private void createParam(Consumer<UpdateQuestionnaireOrdersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateQuestionnaireOrdersUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionnaireOrdersUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new UpdateQuestionnaireOrdersUseCase.QuestionnaireParam(123L, 3),
                new UpdateQuestionnaireOrdersUseCase.QuestionnaireParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
