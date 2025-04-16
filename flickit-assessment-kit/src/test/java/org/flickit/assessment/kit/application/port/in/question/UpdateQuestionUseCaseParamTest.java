package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateQuestionUseCaseParamTest {

    @Test
    void testUpdateQuestionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_QUESTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionUseCaseParam_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + UPDATE_QUESTION_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_QUESTION_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateQuestionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTION_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTION_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class, () ->
            createParam(b -> b.title(RandomStringUtils.randomAlphabetic(251))));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTION_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionUseCaseParam_hintParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.hint("ab")));
        assertThat(throwable).hasMessage("hint: " + UPDATE_QUESTION_HINT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.hint(RandomStringUtils.randomAlphabetic(1001))));
        assertThat(throwable).hasMessage("hint: " + UPDATE_QUESTION_HINT_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionUseCaseParam_mayNotBeApplicableParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.mayNotBeApplicable(null)));
        assertThat(throwable).hasMessage("mayNotBeApplicable: " + UPDATE_QUESTION_MAY_NOT_BE_APPLICABLE_NOT_NULL);
    }

    @Test
    void testUpdateQuestionUseCaseParam_advisableParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.advisable(null)));
        assertThat(throwable).hasMessage("advisable: " + UPDATE_QUESTION_ADVISABLE_NOT_NULL);
    }

    @Test
    void testUpdateQuestionUseCaseParam_translationLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(org.flickit.assessment.common.exception.ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new QuestionTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateQuestionUseCaseParam_translationsFieldViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_QUESTION_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionTranslation(RandomStringUtils.randomAlphabetic(251), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_QUESTION_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].hint: " + TRANSLATION_QUESTION_HINT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new QuestionTranslation("title", RandomStringUtils.randomAlphabetic(1001))))));
        assertThat(throwable).hasMessage("translations[EN].hint: " + TRANSLATION_QUESTION_HINT_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateQuestionUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private UpdateQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionUseCase.Param.builder()
            .kitVersionId(1L)
            .questionId(1L)
            .index(1)
            .title("abc")
            .hint("new hint")
            .mayNotBeApplicable(true)
            .advisable(false)
            .translations(Map.of("EN", new QuestionTranslation("title", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
