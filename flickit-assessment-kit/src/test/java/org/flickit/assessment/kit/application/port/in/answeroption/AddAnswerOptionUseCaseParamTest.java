package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.answeroption.AddAnswerOptionUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddAnswerOptionUseCaseParamTest {

    @Test
    void testAddAnswerOptionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + ADD_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testAddAnswerOptionUseCaseParam_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + ADD_ANSWER_OPTION_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testAddAnswerOptionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + ADD_ANSWER_OPTION_INDEX_NOT_NULL);
    }

    @Test
    void testAddAnswerOptionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + ADD_ANSWER_OPTION_TITLE_NOT_BLANK);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("     ")));
        assertThat(throwable).hasMessage("title: " + ADD_ANSWER_OPTION_TITLE_NOT_BLANK);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(2))));
        assertThat(throwable).hasMessage("title: " + ADD_ANSWER_OPTION_TITLE_SIZE_MIN);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + ADD_ANSWER_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testAddAnswerOptionUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .questionId(56L)
            .index(3)
            .title("first")
            .currentUserId(UUID.randomUUID());
    }
}