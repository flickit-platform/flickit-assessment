package org.flickit.assessment.advice.application.port.in.adviceitem;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAdviceItemUseCaseParamTest {

    @Test
    void testUpdateAdviceItemUseCaseParam_adviceItemIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.adviceItemId(null)));
        assertThat(throwable).hasMessage("adviceItemId: " + UPDATE_ADVICE_ITEM_ID_NOT_NULL);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ADVICE_ITEM_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_ADVICE_ITEM_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_ADVICE_ITEM_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("    h     ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ADVICE_ITEM_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + UPDATE_ADVICE_ITEM_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.random(3001))));
        assertThat(throwable).hasMessage("description: " + UPDATE_ADVICE_ITEM_DESCRIPTION_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("    h     ")));
        assertThat(throwable).hasMessage("description: " + UPDATE_ADVICE_ITEM_DESCRIPTION_SIZE_MIN);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_impactParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.impact(null)));
        assertThat(throwable).hasMessage("impact: " + UPDATE_ADVICE_ITEM_IMPACT_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.impact("h")));
        assertThat(throwable).hasMessage("impact: " + UPDATE_ADVICE_ITEM_IMPACT_INVALID);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_costParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.cost(null)));
        assertThat(throwable).hasMessage("cost: " + UPDATE_ADVICE_ITEM_COST_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.cost("h")));
        assertThat(throwable).hasMessage("cost: " + UPDATE_ADVICE_ITEM_COST_INVALID);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_priorityParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.priority(null)));
        assertThat(throwable).hasMessage("priority: " + UPDATE_ADVICE_ITEM_PRIORITY_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.priority("h")));
        assertThat(throwable).hasMessage("priority: " + UPDATE_ADVICE_ITEM_PRIORITY_INVALID);
    }

    @Test
    void testUpdateAdviceItemUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .assessmentId(UUID.randomUUID())
            .title("title")
            .description("description")
            .cost("LOW")
            .impact("MEDIUM")
            .priority("HIGH")
            .currentUserId(UUID.randomUUID());
    }
}
