package org.flickit.assessment.core.application.port.in.questions;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_QUESTION_ISSUES_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_QUESTION_ISSUES_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetQuestionIssuesUseCaseParamTest {

    @Test
    void testGetQuestionIssuesUseCaseParam_questionIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + GET_QUESTION_ISSUES_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionIssuesUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_QUESTION_ISSUES_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionIssuesUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetQuestionIssuesUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetQuestionIssuesUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionIssuesUseCase.Param.builder()
            .questionId(0L)
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
