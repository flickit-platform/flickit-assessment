package org.flickit.assessment.core.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentQuestionUseCaseParamTest {

    @Test
    void testGetAssessmentQuestionUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_QUESTION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentQuestionUseCaseParam_questionIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentQuestionUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAssessmentQuestionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAssessmentQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentQuestionUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionId(0L)
            .currentUserId(UUID.randomUUID());
    }
}
