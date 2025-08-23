package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_OPTIONS_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_OPTIONS_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetQuestionOptionsUseCaseParamTest {

    @Test
    void testGetQuestionOptionsUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_QUESTION_OPTIONS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionOptionsUseCaseParam_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + GET_QUESTION_OPTIONS_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetQuestionOptionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetQuestionOptionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionOptionsUseCase.Param.builder()
            .kitVersionId(1L)
            .questionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
