package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.APPROVE_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class ApproveAdviceNarrationUseCaseParamTest {

    @Test
    void testApproveAdviceNarrationUseCaseParam_assessmentIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + APPROVE_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testApproveAdviceNarrationUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<ApproveAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private ApproveAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
