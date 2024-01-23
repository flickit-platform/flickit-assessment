package org.flickit.assessment.core.application.port.in.answer;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerUseCaseParamTest {

    @Test
    void testSubmitAnswer_assessmentIdIsNull_ErrorMessage() {
        int confidenceLevelId = ConfidenceLevel.getDefault().getId();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(null, 1L, 1L, 1L, confidenceLevelId, Boolean.FALSE, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + SUBMIT_ANSWER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_questionnaireIdIsNull_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        int confidenceLevelId = ConfidenceLevel.getDefault().getId();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(assessmentResult, null, 1L, 1L, confidenceLevelId, Boolean.FALSE, currentUserId));
        assertThat(throwable).hasMessage("questionnaireId: " + SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_questionIdIsNull_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        int confidenceLevelId = ConfidenceLevel.getDefault().getId();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(assessmentResult, 1L, null, 1L, confidenceLevelId, Boolean.FALSE, currentUserId));
        assertThat(throwable).hasMessage("questionId: " + SUBMIT_ANSWER_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_currentUserIdIsNull_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        int confidenceLevelId = ConfidenceLevel.getDefault().getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerUseCase.Param(assessmentResult, 1L, 1L, 1L, confidenceLevelId, Boolean.FALSE, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
