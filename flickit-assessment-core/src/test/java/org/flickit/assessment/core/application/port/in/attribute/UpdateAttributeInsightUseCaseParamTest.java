package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributeInsightUseCaseParamTest {

    @Test
    void testUpdateAttributeInsight_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var attributeId = 1L;
        var assessorInsight = "Some assessorInsight";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(null, attributeId, assessorInsight, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsight_attributeIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessorInsight = "Some assessorInsight";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, null, assessorInsight, currentUserId));
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
    void testUpdateAttributeInsight_assessorInsightIsLong_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessorInsight = RandomStringUtils.random(1001, true, true);
        var attributeId = 0L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, attributeId, assessorInsight, currentUserId));
        assertThat(throwable).hasMessage("assessorInsight: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeInsight_currentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var assessorInsight = "Some assessorInsight";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, attributeId, assessorInsight, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsight_validPrams_Successful() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessorInsight = "assessorInsight";
        var attributeId = 0L;
        assertDoesNotThrow(()-> new UpdateAttributeInsightUseCase.Param(assessmentId, attributeId, assessorInsight, currentUserId));
    }
}
