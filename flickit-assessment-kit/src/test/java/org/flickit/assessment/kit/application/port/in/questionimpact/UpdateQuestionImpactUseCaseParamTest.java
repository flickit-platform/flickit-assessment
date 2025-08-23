package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQuestionImpactUseCaseParamTest {

    @Test
    void testUpdateQuestionImpactUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionImpactUseCaseParam_questionImpactIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionImpactId(null)));
        assertThat(throwable).hasMessage("questionImpactId: " + UPDATE_QUESTION_IMPACT_QUESTION_IMPACT_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionImpactUseCaseParam_attributeIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_QUESTION_IMPACT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionImpactUseCaseParam_maturityLevelIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + UPDATE_QUESTION_IMPACT_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionImpactUseCaseParam_weightParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.weight(null)));
        assertThat(throwable).hasMessage("weight: " + UPDATE_QUESTION_IMPACT_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateQuestionImpactUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionImpactUseCase.Param.builder()
            .kitVersionId(1L)
            .questionImpactId(2L)
            .attributeId(1L)
            .maturityLevelId(1L)
            .weight(1)
            .currentUserId(UUID.randomUUID());
    }
}
