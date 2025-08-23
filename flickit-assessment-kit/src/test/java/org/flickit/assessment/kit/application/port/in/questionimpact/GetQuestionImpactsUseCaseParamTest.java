package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_IMPACTS_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_IMPACTS_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetQuestionImpactsUseCaseParamTest {

    @Test
    void testGetQuestionImpactsUseCaseParam_questionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + GET_QUESTION_IMPACTS_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionImpactsUseCaseParam_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_QUESTION_IMPACTS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionImpactsUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetQuestionImpactsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetQuestionImpactsUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionImpactsUseCase.Param.builder()
            .questionId(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
