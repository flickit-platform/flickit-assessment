package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAnswerOptionUseCaseParamTest {

    @Test
    void testCreateAnswerOptionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + CREATE_ANSWER_OPTION_QUESTION_NOT_NULL);
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_valueParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + CREATE_ANSWER_OPTION_VALUE_NOT_NULL);
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_ANSWER_OPTION_INDEX_NOT_NULL);
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_ANSWER_OPTION_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("     ")));
        assertThat(throwable).hasMessageContaining("title: " + CREATE_ANSWER_OPTION_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_ANSWER_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new AnswerOptionTranslation("title")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AnswerOptionTranslation("t")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AnswerOptionTranslation(randomAlphabetic(101))))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateAnswerOptionUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
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
            .questionId(153L)
            .index(3)
            .title("first")
            .value(0.5D)
            .translations(Map.of("EN", new AnswerOptionTranslation("title")))
            .currentUserId(UUID.randomUUID());
    }
}
