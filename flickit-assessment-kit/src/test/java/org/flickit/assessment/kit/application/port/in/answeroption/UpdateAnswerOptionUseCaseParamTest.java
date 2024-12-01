package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
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

    private void createParam(Consumer<UpdateAnswerOptionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAnswerOptionUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerOptionUseCase.Param.builder()
            .kitVersionId(1L)
            .answerOptionId(1L)
            .index(1)
            .title("answerOptionTitle")
            .value(1d)
            .currentUserId(UUID.randomUUID());
    }
}
