package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteAnswerRangeUseCaseParamTest {

    @Test
    void testDeleteAnswerRangeUseCaseParam_answerRangeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.answerRangeId(null)));
        assertThat(throwable).hasMessage("answerRangeId: " + DELETE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL);
    }

    @Test
    void testDeleteAnswerRangeUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteAnswerRangeUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private DeleteAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAnswerRangeUseCase.Param.builder()
            .answerRangeId(2L)
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
