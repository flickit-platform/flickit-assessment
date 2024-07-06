package org.flickit.assessment.core.application.port.in.answerhistory;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetAnswerHistoryListUseCaseParamTest {

    Long questionId;
    UUID assessmentId;
    UUID currentUserId;
    int page;
    int size;

    @BeforeEach
    void setUp() {
        questionId = 1L;
        assessmentId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
        size = 1;
        page = 5;
    }

    @Test
    void testGetAnswerHistoryList_assessmentIdIsNull_ErrorMessage() {
        assessmentId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ANSWER_HISTORY_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_questionIdIsNull_ErrorMessage() {
        questionId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page));
        assertThat(throwable).hasMessage("questionId: " + GET_ANSWER_HISTORY_LIST_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerHistoryList_PageSizeIsLessThanMin_ErrorMessage() {
        size = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_ANSWER_HISTORY_LIST_SIZE_MIN);
    }

    @Test
    void testGetAnswerHistoryList_PageSizeIsGreaterThanMax_ErrorMessage() {
        size = 101;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_ANSWER_HISTORY_LIST_SIZE_MAX);
    }

    @Test
    void testGetAnswerHistoryList_PageNumberIsLessThanMin_ErrorMessage() {
        page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page));
        assertThat(throwable).hasMessage("page: " + GET_ANSWER_HISTORY_LIST_PAGE_MIN);
    }
}
