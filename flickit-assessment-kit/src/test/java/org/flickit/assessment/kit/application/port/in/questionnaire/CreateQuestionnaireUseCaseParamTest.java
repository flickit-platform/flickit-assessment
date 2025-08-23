package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateQuestionnaireUseCaseParamTest {

    @Test
    void testCreateQuestionnaireUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_QUESTIONNAIRE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_indexIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_QUESTIONNAIRE_INDEX_NOT_NULL);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTIONNAIRE_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTIONNAIRE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTIONNAIRE_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_QUESTIONNAIRE_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + CREATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + CREATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new QuestionnaireTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_QUESTIONNAIRE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation(RandomStringUtils.randomAlphabetic(101), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_QUESTIONNAIRE_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_QUESTIONNAIRE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation("title", RandomStringUtils.randomAlphabetic(501))))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_QUESTIONNAIRE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_currentUserIdIsNull_ErrorMessage() {
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
            .index(1)
            .title("title")
            .description("description")
            .translations(Map.of("EN", new QuestionnaireTranslation("title", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
