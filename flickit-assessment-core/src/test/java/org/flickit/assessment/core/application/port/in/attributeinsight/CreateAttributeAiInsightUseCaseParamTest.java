package org.flickit.assessment.core.application.port.in.attributeinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ATTRIBUTE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAttributeAiInsightUseCaseParamTest {

    @Test
    void testCreateAttributeAiInsight_AssessmentIdIsNull_ErrorMessage() {
        var attributeId = 1L;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeAiInsightUseCase.Param(null, attributeId, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeAiInsight_AttributeIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeAiInsightUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_ATTRIBUTE_AI_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeAiInsight_CurrentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeAiInsightUseCase.Param(assessmentId, attributeId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
