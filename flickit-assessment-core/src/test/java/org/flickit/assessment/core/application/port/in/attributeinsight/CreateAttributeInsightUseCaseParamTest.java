package org.flickit.assessment.core.application.port.in.attributeinsight;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAttributeInsightUseCaseParamTest {

    @Test
    void testCreateAttributeInsightParam_assessmentIdParamViolateConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeInsightParam_attributeIdParamViolateConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeInsightParam_assessorInsightParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessorInsight(RandomStringUtils.random(1001))));
        assertThat(throwable).hasMessage("assessorInsight: " + CREATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessorInsight(null)));
        assertThat(throwable).hasMessage("assessorInsight: " + CREATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_NOT_NULL);
    }

    @Test
    void testCreateAttributeInsightParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateAttributeInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAttributeInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAttributeInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(0L)
            .assessorInsight("assessorInsight")
            .currentUserId(UUID.randomUUID());
    }
}
