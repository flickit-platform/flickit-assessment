package org.flickit.assessment.core.application.port.in.answerhistory;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAnswerHistoryListUseCaseParamTest {

    @Test
    void testGetAnswerHistoryList_AssessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ANSWER_HISTORY_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_questionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + GET_ANSWER_HISTORY_LIST_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_CurrentUserIdParamViolatesConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_SizeParamViolateConstrains_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_ANSWER_HISTORY_LIST_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_ANSWER_HISTORY_LIST_SIZE_MAX);
    }

    @Test
    void testGetAnswerHistoryList_PageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_ANSWER_HISTORY_LIST_PAGE_MIN);
    }

    private void createParam(Consumer<GetAnswerHistoryListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAnswerHistoryListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAnswerHistoryListUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionId(2L)
            .size(10)
            .page(1)
            .currentUserId(UUID.randomUUID());
    }
}
