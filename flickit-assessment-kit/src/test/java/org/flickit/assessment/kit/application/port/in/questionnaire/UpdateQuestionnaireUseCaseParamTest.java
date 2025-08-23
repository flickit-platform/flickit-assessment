package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateQuestionnaireUseCaseParamTest {

    @Test
    void testUpdateQuestionnaireUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_QUESTIONNAIRE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireUseCaseParam_questionnaireIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + UPDATE_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_QUESTIONNAIRE_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTIONNAIRE_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTIONNAIRE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTIONNAIRE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionnaireUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + UPDATE_QUESTIONNAIRE_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + UPDATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + UPDATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionnaireUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new QuestionnaireTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateQuestionnaireUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation("t", "desc")))));
        Assertions.assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_QUESTIONNAIRE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation(RandomStringUtils.randomAlphabetic(101), "desc")))));
        Assertions.assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_QUESTIONNAIRE_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation("title", "de")))));
        Assertions.assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_QUESTIONNAIRE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionnaireTranslation("title", RandomStringUtils.randomAlphabetic(501))))));
        Assertions.assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_QUESTIONNAIRE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionnaireParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        Assertions.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateQuestionnaireUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateQuestionnaireUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionnaireUseCase.Param.builder()
            .kitVersionId(1L)
            .questionnaireId(1L)
            .title("abc")
            .index(1)
            .description("description")
            .translations(Map.of("EN", new QuestionnaireTranslation("title", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
