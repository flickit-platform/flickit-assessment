package org.flickit.assessment.core.application.port.in.attributeinsight;

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
    void testUpdateAttributeInsightParam_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(null, 123L, "Some assessorInsight", currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsightParam_attributeIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, null, "Some assessorInsight", currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsightParam_assessorInsightIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, 123L, null, currentUserId));
        assertThat(throwable).hasMessage("assessorInsight: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsightParam_assessorInsightIsGreaterThanMax_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessorInsight = RandomStringUtils.random(1001, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, 123L, assessorInsight, currentUserId));
        assertThat(throwable).hasMessage("assessorInsight: " + UPDATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeInsightParam_currentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAttributeInsightUseCase.Param(assessmentId, 123L, "Some assessorInsight", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeInsightParam_validPrams_Successful() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        assertDoesNotThrow(()-> new UpdateAttributeInsightUseCase.Param(assessmentId, 123L, "Some assessorInsight", currentUserId));
    }
}
