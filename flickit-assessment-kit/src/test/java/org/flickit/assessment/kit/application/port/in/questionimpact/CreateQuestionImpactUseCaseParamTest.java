package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateQuestionImpactUseCaseParamTest {

    @Test
    void testCreateQuestionImpactUseCaseParam_kitVersionIdViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionImpactUseCaseParam_attributeIdViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_QUESTION_IMPACT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionImpactUseCaseParam_maturityLevelIdViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + CREATE_QUESTION_IMPACT_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionImpactUseCaseParam_questionIdViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionId(null)));
        assertThat(throwable).hasMessage("questionId: " + CREATE_QUESTION_IMPACT_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionImpactUseCaseParam_weightViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.weight(null)));
        assertThat(throwable).hasMessage("weight: " + CREATE_QUESTION_IMPACT_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateQuestionImpactUseCaseParam_currentUserIdViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return new CreateQuestionImpactUseCase.Param.ParamBuilder()
            .kitVersionId(1L)
            .attributeId(2L)
            .maturityLevelId(3L)
            .questionId(4L)
            .weight(1)
            .currentUserId(UUID.randomUUID());
    }
}
