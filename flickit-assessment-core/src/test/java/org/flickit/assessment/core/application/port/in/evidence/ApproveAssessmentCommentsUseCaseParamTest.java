package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ASSESSMENT_COMMENTS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class ApproveAssessmentCommentsUseCaseParamTest {

    @Test
    void testApproveAssessmentCommentsUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + APPROVE_ASSESSMENT_COMMENTS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testApproveAssessmentCommentsUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<ApproveAssessmentCommentsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private ApproveAssessmentCommentsUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAssessmentCommentsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
