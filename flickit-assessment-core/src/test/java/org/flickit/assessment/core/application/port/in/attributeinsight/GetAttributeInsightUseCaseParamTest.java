package org.flickit.assessment.core.application.port.in.attributeinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAttributeInsightUseCaseParamTest {

    @Test
    void testGetAttributeInsight_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var attributeId = 1L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeInsightUseCase.Param(null, attributeId, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeInsight_attributeIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeInsightUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeInsight_currentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeInsightUseCase.Param(assessmentId, attributeId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
