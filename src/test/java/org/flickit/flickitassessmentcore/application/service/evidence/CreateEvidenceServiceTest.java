package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.evidence.CreateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateEvidenceServiceTest {

    public static final String DESC = "desc";
    private final CreateEvidencePort createEvidence = Mockito.mock(CreateEvidencePort.class);

    private final CreateEvidenceService service = new CreateEvidenceService(
        createEvidence
    );

    private final Evidence evidence = new Evidence(
        null,
        DESC,
        LocalDateTime.now(),
        LocalDateTime.now(),
        1L,
        UUID.randomUUID(),
        1L
    );

    @Test
    void createEvidence_ValidCommand_SavesAndReturnsEvidence() {
        when(createEvidence.createEvidence(any(CreateEvidencePort.Param.class))).thenReturn(new CreateEvidencePort.Result(evidence));

        CreateEvidenceUseCase.Result result = service.createEvidence(new CreateEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        ));

        assertNotNull(result.evidence());
    }

    @Test
    void createEvidence_EmptyDesc_ReturnsErrorMessage() {
        evidence.setDescription("");
        when(createEvidence.createEvidence(any(CreateEvidencePort.Param.class))).thenReturn(new CreateEvidencePort.Result(evidence));

        assertThrows(ConstraintViolationException.class, () -> service.createEvidence(new CreateEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setDescription(DESC);
    }

    @Test
    void createEvidence_NullCreatedById_ReturnsErrorMessage() {
        evidence.setCreatedById(null);
        when(createEvidence.createEvidence(any(CreateEvidencePort.Param.class))).thenReturn(new CreateEvidencePort.Result(evidence));

        assertThrows(ConstraintViolationException.class, () -> service.createEvidence(new CreateEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setCreatedById(1L);
    }

    @Test
    void createEvidence_NullAssessmentId_ReturnsErrorMessage() {
        evidence.setAssessmentId(null);
        when(createEvidence.createEvidence(any(CreateEvidencePort.Param.class))).thenReturn(new CreateEvidencePort.Result(evidence));

        assertThrows(ConstraintViolationException.class, () -> service.createEvidence(new CreateEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setAssessmentId(UUID.randomUUID());
    }

    @Test
    void createEvidence_NullQuestionId_ReturnsErrorMessage() {
        evidence.setQuestionId(null);
        when(createEvidence.createEvidence(any(CreateEvidencePort.Param.class))).thenReturn(new CreateEvidencePort.Result(evidence));

        assertThrows(ConstraintViolationException.class, () -> service.createEvidence(new CreateEvidenceUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setQuestionId(1L);
    }
}
