package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentAttributeMeasuresUseCaseParamTest {

    @Test
    void testGetAssessmentAttributeMeasuresUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertEquals("assessmentId: " + GET_ASSESSMENT_ATTRIBUTE_MEASURES_ASSESSMENT_ID_NOT_NULL, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAttributeMeasuresUseCaseParam_attributeIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertEquals("attributeId: " + GET_ASSESSMENT_ATTRIBUTE_MEASURES_ATTRIBUTE_ID_NOT_NULL, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAttributeMeasuresUseCaseParam_sortParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.sort("invalid")));
        assertEquals("sort: " + GET_ASSESSMENT_ATTRIBUTE_MEASURES_SORT_INVALID, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAttributeMeasuresUseCaseParam_orderParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.order("invalid")));
        assertEquals("order: " + GET_ASSESSMENT_ATTRIBUTE_MEASURES_INVALID, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAttributeMeasuresUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertEquals("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL, throwable.getMessage());
    }

    private void createParam(Consumer<Param.ParamBuilder> changes) {
        Param.ParamBuilder paramBuilder = paramBuilder();
        changes.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(554L)
            .sort("impact_percentage")
            .order("ASC")
            .currentUserId(UUID.randomUUID());
    }
}
