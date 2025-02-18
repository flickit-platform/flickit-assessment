package org.flickit.assessment.core.application.port.in.attributeinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ATTRIBUTE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAttributeAiInsightUseCaseParamTest {

    @Test
    void testCreateAttributeAiInsightUseCaseParam_assessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeAiInsightUseCaseParam_attributeIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_ATTRIBUTE_AI_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeAiInsightUseCaseParam_currentUserIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateAttributeAiInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAttributeAiInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAttributeAiInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
