package org.flickit.assessment.core.application.port.in.answer;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.port.in.answer.ApproveAnswerUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ANSWER_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ANSWER_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApproveAnswerUseCaseParamTest {

    @Test
    void testApproveAnswerUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + APPROVE_ANSWER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testApproveAnswerUseCaseParam_pageParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + APPROVE_ANSWER_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testApproveAnswerUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionId(1563L)
            .currentUserId(UUID.randomUUID());
    }
}