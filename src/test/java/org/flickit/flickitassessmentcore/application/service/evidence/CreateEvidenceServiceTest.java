package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.AddEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateEvidenceServiceTest {

    public static final String DESC = "desc";
    private final Evidence evidence = new Evidence(
        UUID.randomUUID(),
        DESC,
        LocalDateTime.now(),
        LocalDateTime.now(),
        1L,
        UUID.randomUUID(),
        1L
    );
    @Mock
    private AddEvidencePort addEvidence;
    @Spy
    @InjectMocks
    private AddEvidenceService service;

    @Test
    void addEvidence_ValidCommand_SavesAndReturnsEvidence() {
        when(addEvidence.addEvidence(any(AddEvidencePort.Param.class))).thenReturn(new AddEvidencePort.Result(evidence.getId()));

        AddEvidenceUseCase.Result result = service.addEvidence(new AddEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        ));

        assertNotNull(result.id());
        assertEquals(evidence.getId(), result.id());
    }

    @Test
    void addEvidence_EmptyDesc_ReturnsErrorMessage() {
        evidence.setDescription("");

        assertThrows(ConstraintViolationException.class, () -> service.addEvidence(new AddEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )), ADD_EVIDENCE_DESC_NOT_BLANK);

        evidence.setDescription(DESC);
    }

    @Test
    void addEvidence_NullCreatedById_ReturnsErrorMessage() {
        evidence.setCreatedById(null);

        assertThrows(ConstraintViolationException.class, () -> service.addEvidence(new AddEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )), ADD_EVIDENCE_CREATED_BY_ID_NOT_NULL);

        evidence.setCreatedById(1L);
    }

    @Test
    void addEvidence_NullAssessmentId_ReturnsErrorMessage() {
        evidence.setAssessmentId(null);

        assertThrows(ConstraintViolationException.class, () -> service.addEvidence(new AddEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )), ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL);

        evidence.setAssessmentId(UUID.randomUUID());
    }

    @Test
    void addEvidence_NullQuestionId_ReturnsErrorMessage() {
        evidence.setQuestionId(null);

        assertThrows(ConstraintViolationException.class, () -> service.addEvidence(new AddEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )), ADD_EVIDENCE_QUESTION_ID_NOT_NULL);

        evidence.setQuestionId(1L);
    }
}
