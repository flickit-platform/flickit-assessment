package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_OPTION_ANSWER_OPTION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteAnswerOptionUseCaseParamTest {

    @Test
    void testDeleteAnswerOptionUseCaseParam_answerOptionIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.answerOptionId(null)));
        assertThat(throwable).hasMessage("answerOptionId: " + DELETE_ANSWER_OPTION_ANSWER_OPTION_ID_NOT_NULL);
    }

    @Test
    void testDeleteAnswerOptionUseCaseParam_kitVersionIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteAnswerOptionUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteAnswerOptionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteAnswerOptionUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAnswerOptionUseCase.Param.builder()
            .answerOptionId(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
