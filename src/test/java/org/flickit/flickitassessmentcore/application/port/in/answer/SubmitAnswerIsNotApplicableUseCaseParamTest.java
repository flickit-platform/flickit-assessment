package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerIsNotApplicableUseCaseParamTest {

    @Test
    void submitAnswer_NullAssessmentResult_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerIsNotApplicableUseCase.Param(null, 1L, 1L, Boolean.FALSE));
        assertThat(throwable).hasMessage("assessmentResultId: " + SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_NULL);
    }

    @Test
    void submitAnswer_NullQuestionnaireId_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerIsNotApplicableUseCase.Param(assessmentResult, null, 1L, Boolean.FALSE));
        assertThat(throwable).hasMessage("questionnaireId: " + SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void submitAnswer_NullQuestionId_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerIsNotApplicableUseCase.Param(assessmentResult, 1L, null, Boolean.FALSE));
        assertThat(throwable).hasMessage("questionId: " + SUBMIT_ANSWER_QUESTION_ID_NOT_NULL);
    }

    @Test
    void submitAnswer_NullIsNotApplicable_ErrorMessage() {
        var assessmentResult = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SubmitAnswerIsNotApplicableUseCase.Param(assessmentResult, 1L, 1L, null));
        assertThat(throwable).hasMessage("isNotApplicable: " + SUBMIT_ANSWER_IS_NOT_APPLICABLE_NOT_NULL);
    }
}
