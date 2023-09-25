package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.DeleteEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteEvidenceServiceTest {

    @Mock
    private DeleteEvidencePort deleteEvidencePort;

    @Spy
    @InjectMocks
    private DeleteEvidenceService service;

    private final Evidence evidence = new Evidence(
        UUID.randomUUID(),
        "Description",
        1L,
        UUID.randomUUID(),
        1L,
        LocalDateTime.now(),
        LocalDateTime.now()
    );

    @Test
    void deleteEvidence_IdGiven_Delete() {
        doNothing().when(deleteEvidencePort).deleteEvidence(new DeleteEvidencePort.Param(evidence.getId()));
        service.deleteEvidence(new DeleteEvidenceUseCase.Param(evidence.getId()));
        verify(deleteEvidencePort).deleteEvidence(new DeleteEvidencePort.Param(evidence.getId()));
    }

    @Test
    void deleteEvidence_IdIsNull_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.deleteEvidence(new DeleteEvidenceUseCase.Param(null)),
            ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void deleteEvidence_IdGivenButEvidenceNotExist_ErrorMessage() {
        doThrow(ResourceNotFoundException.class).when(deleteEvidencePort).deleteEvidence(any());
        assertThrows(ResourceNotFoundException.class,
            () -> service.deleteEvidence(new DeleteEvidenceUseCase.Param(evidence.getId())),
            ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_NOT_FOUND);
    }
}
