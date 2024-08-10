package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAssessmentAttributeInsightUseCaseParamTest {

    @Test
    void testUpdateAssessmentAttributeInsight_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var attributeId = 1L;
        var content = "Some content";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentAttributeInsightUseCase.Param(null, attributeId, content, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentAttributeInsight_attributeIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var content = "Some content";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentAttributeInsightUseCase.Param(assessmentId, null, content, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentAttributeInsight_contentIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var attributeId = 0L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentAttributeInsightUseCase.Param(assessmentId, attributeId, null, currentUserId));
        assertThat(throwable).hasMessage("content: " + UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_CONTENT_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentAttributeInsight_CurrentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var content = "Some content";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentAttributeInsightUseCase.Param(assessmentId, attributeId, content, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
