package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateAnswerRangeUseCaseParamTest {

    @Test
    void testUpdateAnswerRangeUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_answerRangeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.answerRangeId(null)));
        assertThat(throwable).hasMessage("answerRangeId: " + UPDATE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_RANGE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_RANGE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_reusableParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.reusable(null)));
        assertThat(throwable).hasMessage("reusable: " + UPDATE_ANSWER_RANGE_REUSABLE_NOT_NULL);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new AnswerRangeTranslation("title")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AnswerRangeTranslation("t")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ANSWER_RANGE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AnswerRangeTranslation(RandomStringUtils.randomAlphabetic(101))))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ANSWER_RANGE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private UpdateAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerRangeUseCase.Param.builder()
            .kitVersionId(1L)
            .answerRangeId(2L)
            .title("title")
            .reusable(true)
            .currentUserId(UUID.randomUUID());
    }
}
