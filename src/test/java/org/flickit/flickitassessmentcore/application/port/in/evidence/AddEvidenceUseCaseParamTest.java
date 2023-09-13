package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddEvidenceUseCaseParamTest {

    @Test
    void addEvidence_BlankDesc_ReturnsErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param(
                "    ",
                1L,
                assessmentId,
                1L
            ));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_NOT_BLANK);
    }

    @Test
    void addEvidence_InvalidDescMinSize_ReturnsErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param(
                "ab",
                1L,
                assessmentId,
                1L
            ));
        assertThat(throwable).hasMessage("description: " + ADD_EVIDENCE_DESC_SIZE_MIN);
    }

    @Test
    void addEvidence_NullCreatedById_ReturnsErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class, () -> new AddEvidenceUseCase.Param(
            "desc",
            null,
            assessmentId,
            1L
        ));
        assertThat(throwable).hasMessage("createdById: " + ADD_EVIDENCE_CREATED_BY_ID_NOT_NULL);
    }

    @Test
    void addEvidence_NullAssessmentId_ReturnsErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> new AddEvidenceUseCase.Param(
            "desc",
            1L,
            null,
            1L
        ));
        assertThat(throwable).hasMessage("assessmentId: " + ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void addEvidence_NullQuestionId_ReturnsErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new AddEvidenceUseCase.Param(
                "desc",
                1L,
                assessmentId,
                null
            )
        );
        assertThat(throwable).hasMessage("questionId: " + ADD_EVIDENCE_QUESTION_ID_NOT_NULL);
    }
}
