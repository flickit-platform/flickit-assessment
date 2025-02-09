package org.flickit.assessment.core.application.port.in.answer;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerUseCaseParamTest {

    @Test
    void testSubmitAnswer_assessmentIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + SUBMIT_ANSWER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_questionnaireIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionnaireId(null)));
        assertThat(throwable).hasMessage("questionnaireId: " + SUBMIT_ANSWER_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_questionIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + SUBMIT_ANSWER_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testSubmitAnswer_currentUserIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<SubmitAnswerUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private SubmitAnswerUseCase.Param.ParamBuilder paramBuilder() {
        return SubmitAnswerUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(15L)
            .questionId(163L)
            .answerOptionId(18663L)
            .confidenceLevelId(ConfidenceLevel.getDefault().getId())
            .isNotApplicable(Boolean.FALSE)
            .currentUserId(UUID.randomUUID());
    }
}
