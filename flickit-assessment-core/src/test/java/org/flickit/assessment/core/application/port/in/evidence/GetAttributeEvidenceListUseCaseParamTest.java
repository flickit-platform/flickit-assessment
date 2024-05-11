package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAttributeEvidenceListUseCaseParamTest {

    @Test
    void testGetAttributeEvidenceListParam_NullAssessmentId_ReturnErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeEvidenceListUseCase.Param(null, 123L, "POSITIVE", currentUserId, 10, 0));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ATTRIBUTE_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeEvidenceListParam_NullAttributeId_ReturnErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeEvidenceListUseCase.Param(assessmentId, null, "POSITIVE", currentUserId, 10, 0));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_EVIDENCE_LIST_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeEvidenceListParam_NullEvidenceType_ReturnErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeEvidenceListUseCase.Param(assessmentId, 123L, null, currentUserId, 10, 0));
        assertThat(throwable).hasMessage("type: " + GET_ATTRIBUTE_EVIDENCE_LIST_TYPE_NOT_NULL);
    }

    @Test
    void testGetAttributeEvidenceListParam_InvalidEvidenceType_ReturnErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeEvidenceListUseCase.Param(assessmentId, 123L, "positive", currentUserId, 10, 0));
        assertThat(throwable).hasMessage("type: " + GET_ATTRIBUTE_EVIDENCE_LIST_TYPE_INVALID);
    }

    @Test
    void testGetAttributeEvidenceListParam_NullCurrentUserId_ReturnErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeEvidenceListUseCase.Param(assessmentId, 123L, "POSITIVE", null, 10, 0));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
