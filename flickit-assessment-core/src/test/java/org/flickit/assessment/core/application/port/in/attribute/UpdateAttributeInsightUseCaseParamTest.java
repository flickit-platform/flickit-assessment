package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributeInsightUseCaseParamTest {

    @Test
    void testUpdateAttributeInsight_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var attributeId = 1L;
        var content = "Some content";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(null, attributeId, content, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsight_attributeIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var content = "Some content";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, null, content, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsight_assessorInsightIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var attributeId = 0L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, attributeId, null, currentUserId));
        assertThat(throwable).hasMessage("assessorInsight: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsight_CurrentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var content = "Some content";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, attributeId, content, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
