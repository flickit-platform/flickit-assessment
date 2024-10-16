package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteQuestionUseCaseParamTest {

    @Test
    void testDeleteQuestionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_QUESTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteQuestionUseCaseParam_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + DELETE_QUESTION_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testDeleteQuestionUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        Assertions.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteQuestionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteQuestionUseCase.Param.builder()
            .kitVersionId(1L)
            .questionId(2L)
            .currentUserId(UUID.randomUUID());
    }

}
