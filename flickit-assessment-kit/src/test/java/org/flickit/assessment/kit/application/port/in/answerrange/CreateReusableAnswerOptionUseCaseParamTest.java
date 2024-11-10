package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateReusableAnswerOptionUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateReusableAnswerOptionUseCaseParamTest {

    @Test
    void testCreateReusableAnswerOptionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_REUSABLE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateReusableAnswerOptionUseCaseParam_answerRangeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.answerRangeId(null)));
        assertThat(throwable).hasMessage("answerRangeId: " + CREATE_REUSABLE_ANSWER_OPTION_ANSWER_RANGE_ID_NOT_NULL);
    }

    @Test
    void testCreateReusableAnswerOptionUseCaseParam_valueParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + CREATE_REUSABLE_ANSWER_OPTION_VALUE_NOT_NULL);
    }

    @Test
    void testCreateReusableAnswerOptionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_REUSABLE_ANSWER_OPTION_INDEX_NOT_NULL);
    }

    @Test
    void testCreateReusableAnswerOptionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_REUSABLE_ANSWER_OPTION_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("     ")));
        assertThat(throwable).hasMessageContaining("title: " + CREATE_REUSABLE_ANSWER_OPTION_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_REUSABLE_ANSWER_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateReusableAnswerOptionUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .answerRangeId(153L)
            .index(3)
            .title("first")
            .value(0.5D)
            .currentUserId(UUID.randomUUID());
    }
}
