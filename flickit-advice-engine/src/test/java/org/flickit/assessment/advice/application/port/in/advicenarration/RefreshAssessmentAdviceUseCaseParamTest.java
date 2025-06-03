package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.REFRESH_ASSESSMENT_ADVICE_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.advice.common.ErrorMessageKey.REFRESH_ASSESSMENT_ADVICE_FORCE_REGENERATE_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class RefreshAssessmentAdviceUseCaseParamTest {

    @Test
    void testRefreshAssessmentAdviceUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + REFRESH_ASSESSMENT_ADVICE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testRefreshAssessmentAdviceUseCaseParam_forceRegenerateParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.forceRegenerate(null)));
        assertThat(throwable).hasMessage("forceRegenerate: " + REFRESH_ASSESSMENT_ADVICE_FORCE_REGENERATE_NOT_NULL);
    }

    @Test
    void testRefreshAssessmentAdviceUseCaseParam_forceRegenerateParamIsValid_successful() {
        assertDoesNotThrow(() -> createParam(b -> b.forceRegenerate(false)));
    }

    @Test
    void testRefreshAssessmentAdviceUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<RefreshAssessmentAdviceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private RefreshAssessmentAdviceUseCase.Param.ParamBuilder paramBuilder() {
        return RefreshAssessmentAdviceUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .forceRegenerate(true)
            .currentUserId(UUID.randomUUID());
    }
}
