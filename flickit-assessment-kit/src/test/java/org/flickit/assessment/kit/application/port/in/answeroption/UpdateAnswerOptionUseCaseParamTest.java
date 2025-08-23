package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.answeroption.UpdateAnswerOptionUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAnswerOptionUseCaseParamTest {

    @Test
    void testUpdateAnswerOptionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_answerOptionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.answerOptionId(null)));
        assertThat(throwable).hasMessage("answerOptionId: " + UPDATE_ANSWER_OPTION_ANSWER_OPTION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_ANSWER_OPTION_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_OPTION_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(" ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_OPTION_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_valueParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + UPDATE_ANSWER_OPTION_VALUE_NOT_NULL);
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new AnswerOptionTranslation("title")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AnswerOptionTranslation("t")))));
        Assertions.assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AnswerOptionTranslation(randomAlphabetic(101))))));
        Assertions.assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAnswerOptionUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        Assertions.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .answerOptionId(1L)
            .index(1)
            .title("answerOptionTitle")
            .value(1d)
            .translations(Map.of("EN", new AnswerOptionTranslation("title")))
            .currentUserId(UUID.randomUUID());
    }
}
