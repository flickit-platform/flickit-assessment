package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeOptionUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAnswerRangeOptionUseCaseParamTest {

    @Test
    void testCreateAnswerRangeOptionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_ANSWER_RANGE_OPTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateAnswerRangeOptionUseCaseParam_answerRangeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.answerRangeId(null)));
        assertThat(throwable).hasMessage("answerRangeId: " + CREATE_ANSWER_RANGE_OPTION_ANSWER_RANGE_ID_NOT_NULL);
    }

    @Test
    void testCreateAnswerRangeOptionUseCaseParam_valueParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + CREATE_ANSWER_RANGE_OPTION_VALUE_NOT_NULL);
    }

    @Test
    void testCreateAnswerRangeOptionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_ANSWER_RANGE_OPTION_INDEX_NOT_NULL);
    }

    @Test
    void testCreateAnswerRangeOptionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_ANSWER_RANGE_OPTION_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("     ")));
        assertThat(throwable).hasMessageContaining("title: " + CREATE_ANSWER_RANGE_OPTION_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_ANSWER_RANGE_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateAnswerRangeOptionUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
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
