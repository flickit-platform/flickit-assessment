package org.flickit.assessment.core.application.port.in.measure;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAttributeMeasureQuestionsUseCaseParamTest {

    @Test
    void testGetAttributeMeasureQuestionsUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ATTRIBUTE_MEASURE_QUESTIONS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeMeasureQuestionsUseCaseParam_attributeIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_MEASURE_QUESTIONS_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeMeasureQuestionsUseCaseParam_measureIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.measureId(null)));
        assertThat(throwable).hasMessage("measureId: " + GET_ATTRIBUTE_MEASURE_QUESTIONS_MEASURE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeMeasureQuestionsUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeMeasureQuestionsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .measureId(3L)
            .currentUserId(UUID.randomUUID());
    }
}
