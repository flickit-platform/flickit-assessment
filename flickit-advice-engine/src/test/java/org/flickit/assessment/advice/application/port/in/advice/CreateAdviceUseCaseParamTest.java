package org.flickit.assessment.advice.application.port.in.advice;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAdviceUseCaseParamTest {

    @Test
    void testCreateAdviceUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ADVICE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAdviceUseCaseParam_attributeLevelTargetsParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeLevelTargets(List.of())));
        assertThat(throwable).hasMessage("attributeLevelTargets: " + CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeLevelTargets(null)));
        assertThat(throwable).hasMessage("attributeLevelTargets: " + CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL);
    }

    @Test
    void testCreateAdviceUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateAdviceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAdviceUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAdviceUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeLevelTargets(List.of(new AttributeLevelTarget(1L, 2L)))
            .currentUserId(UUID.randomUUID());
    }
}
