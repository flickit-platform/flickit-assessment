package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentQuestionnaireQuestionListUseCaseParamTest {

    @Test
    void testGetAssessmentQuestionnaireQuestionList_NullAssessmentId_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentQuestionnaireQuestionListUseCase.Param(
                null, 1L, 20, 0, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_NullQuestionnaireId_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentQuestionnaireQuestionListUseCase.Param(
                assessmentId, null, 20, 0, currentUserId));
        assertThat(throwable).hasMessage("questionnaireId: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_PageSizeIsLessThanMin_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentQuestionnaireQuestionListUseCase.Param(
                assessmentId, 1L, 0, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_SIZE_MIN);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_PageSizeIsGreaterThanMax_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentQuestionnaireQuestionListUseCase.Param(
                assessmentId, 1L, 101, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_SIZE_MAX);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_PageNumberIsLessThanMin_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentQuestionnaireQuestionListUseCase.Param(
                assessmentId, 1L, 20, -1, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_PAGE_MIN);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_NullCurrentUserId_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentQuestionnaireQuestionListUseCase.Param(
                assessmentId, 1L, 20, 0, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
