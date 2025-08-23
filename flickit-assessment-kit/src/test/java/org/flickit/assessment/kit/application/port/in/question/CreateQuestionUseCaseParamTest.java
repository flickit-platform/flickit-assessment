package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateQuestionUseCaseParamTest {

    @Test
    void testCreateQuestionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_QUESTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_QUESTION_INDEX_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTION_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTION_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(251))));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTION_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateQuestionUseCaseParam_hintParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.hint("ab")));
        assertThat(throwable).hasMessage("hint: " + CREATE_QUESTION_HINT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.hint(RandomStringUtils.randomAlphabetic(1001))));
        assertThat(throwable).hasMessage("hint: " + CREATE_QUESTION_HINT_SIZE_MAX);
    }

    @Test
    void testCreateQuestionUseCaseParam_mayNotBeApplicableParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.mayNotBeApplicable(null)));
        assertThat(throwable).hasMessage("mayNotBeApplicable: " + CREATE_QUESTION_MAY_NOT_BE_APPLICABLE_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCaseParam_advisableParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.advisable(null)));
        assertThat(throwable).hasMessage("advisable: " + CREATE_QUESTION_ADVISABLE_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCaseParam_questionnaireIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + CREATE_QUESTION_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateQuestionUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private CreateQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return CreateQuestionUseCase.Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("abc")
            .mayNotBeApplicable(true)
            .advisable(false)
            .questionnaireId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
