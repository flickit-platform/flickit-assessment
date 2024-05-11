package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetEvidenceListUseCaseParamTest {

    @Test
    void testGetEvidenceListParam_NullQuestion_ReturnErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(null, assessmentId, 10, 0, currentUserId));
        assertThat(throwable).hasMessage("questionId: " + GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetEvidenceListParam_NullAssessment_ReturnErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(0L, null, 10, 0, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetEvidenceListParam_sizeLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var size = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(0L, assessmentId, size, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_EVIDENCE_LIST_SIZE_MIN);
    }

    @Test
    void testGetEvidenceListParam_sizeGreaterThanMax_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var size = 101;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(0L, assessmentId, size, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_EVIDENCE_LIST_SIZE_MAX);
    }

    @Test
    void testGetEvidenceListParam_PageLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(0L, assessmentId, 10, page, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_EVIDENCE_LIST_PAGE_MIN);
    }

    @Test
    void testGetEvidenceListParam_NullCurrentUserId_ReturnErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(0L, assessmentId, 10, 0, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
