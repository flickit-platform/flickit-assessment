package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddEvidenceUseCaseParamTest {

    @Test
    void testAddEvidenceParam_DescriptionIsBlank_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID createdById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("    ", createdById, assessmentId, 1L));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_NOT_BLANK);
    }

    @Test
    void testAddEvidenceParam_DescriptionIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID createdById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("ab", createdById, assessmentId, 1L));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_SIZE_MIN);
    }

    @Test
    void testAddEvidenceParam_DescriptionSizeIsEqualToMin_Success() {
        UUID createdById = UUID.randomUUID();
        assertDoesNotThrow(
            () -> new AddEvidenceUseCase.Param("abc", createdById, UUID.randomUUID(), 1L));
    }

    @Test
    void testAddEvidenceParam_DescriptionSizeIsGreaterThanMax_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        UUID createdById = UUID.randomUUID();
        var desc = randomAlphabetic(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param(desc, createdById, assessmentId, 1L));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_SIZE_MAX);
    }

    @Test
    void testAddEvidenceParam_DescriptionSizeIsEqualToMax_Success() {
        UUID createdById = UUID.randomUUID();
        assertDoesNotThrow(
            () -> new AddEvidenceUseCase.Param(randomAlphabetic(1000), createdById, UUID.randomUUID(), 1L));
    }

    @Test
    void testAddEvidenceParam_CreatedByIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("desc", null, assessmentId, 1L));
        assertThat(throwable).hasMessage("createdById: " + ADD_EVIDENCE_CREATED_BY_ID_NOT_NULL);
    }

    @Test
    void testAddEvidenceParam_AssessmentIdIsNull_ErrorMessage() {
        UUID createdById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("desc", createdById, null, 1L));
        assertThat(throwable).hasMessage("assessmentId: " + ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testAddEvidenceParam_QuestionIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID createdById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param("desc", createdById, assessmentId, null)
        );
        assertThat(throwable).hasMessage("questionId: " + ADD_EVIDENCE_QUESTION_ID_NOT_NULL);
    }
}
