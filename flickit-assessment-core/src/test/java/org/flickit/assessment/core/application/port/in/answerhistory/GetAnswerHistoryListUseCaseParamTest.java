package org.flickit.assessment.core.application.port.in.answerhistory;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAnswerHistoryListUseCaseParamTest {

    @Test
    void testGetAnswerHistoryList_assessmentIdIsNull_ErrorMessage() {
        UUID assessmentId = null;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, 123L, currentUserId, 5, 1));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ANSWER_HISTORY_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_questionIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, null, currentUserId, 5, 1));
        assertThat(throwable).hasMessage("questionId: " + GET_ANSWER_HISTORY_LIST_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, 123L, null, 5, 1));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_PageSizeIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, 123L, currentUserId, 0, 1));
        assertThat(throwable).hasMessage("size: " + GET_ANSWER_HISTORY_LIST_SIZE_MIN);
    }

    @Test
    void testGetAnswerHistoryList_PageSizeIsGreaterThanMax_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, 123L, currentUserId, 101, 1));
        assertThat(throwable).hasMessage("size: " + GET_ANSWER_HISTORY_LIST_SIZE_MAX);
    }

    @Test
    void testGetAnswerHistoryList_PageNumberIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, 123L, currentUserId, 5, -1));
        assertThat(throwable).hasMessage("page: " + GET_ANSWER_HISTORY_LIST_PAGE_MIN);
    }
}
