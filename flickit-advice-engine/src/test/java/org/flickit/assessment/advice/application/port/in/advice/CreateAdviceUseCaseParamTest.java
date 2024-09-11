package org.flickit.assessment.advice.application.port.in.advice;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAdviceUseCaseParamTest {

    @Test
    void testCreateAdviceParam_AssessmentIdIsNull_ErrorMessage() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAdviceUseCase.Param(null, attributeLevelTargets, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAdviceParam_AttributeLevelTargetsIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAdviceUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("attributeLevelTargets: " + CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL);
    }

    @Test
    void testCreateAdviceParam_AttributeLevelTargetsIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        List<AttributeLevelTarget> attributeLevelTargets = List.of();
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAdviceUseCase.Param(assessmentId, attributeLevelTargets, currentUserId));
        assertThat(throwable).hasMessage("attributeLevelTargets: " + CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);
    }

    @Test
    void testCreateAdviceParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAdviceUseCase.Param(assessmentId, attributeLevelTargets, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
