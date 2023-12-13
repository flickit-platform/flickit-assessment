package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.CheckEvidenceExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.DeleteEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteEvidenceServiceTest {

    @InjectMocks
    private DeleteEvidenceService service;

    @Mock
    private DeleteEvidencePort deleteEvidencePort;

    @Mock
    private CheckEvidenceExistencePort checkEvidenceExistencePort;

    @Test
    void testDeleteEvidence_IdGiven_Delete() {
        UUID evidenceId = UUID.randomUUID();
        doNothing().when(deleteEvidencePort).deleteById(evidenceId);
        when(checkEvidenceExistencePort.existsById(evidenceId)).thenReturn(Boolean.TRUE);
        service.deleteEvidence(new DeleteEvidenceUseCase.Param(evidenceId));

        ArgumentCaptor<UUID> idDeletePortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(deleteEvidencePort).deleteById(idDeletePortArgument.capture());

        assertEquals(evidenceId, idDeletePortArgument.getValue());

        ArgumentCaptor<UUID> idCheckPortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(checkEvidenceExistencePort).existsById(idCheckPortArgument.capture());

        assertEquals(evidenceId, idCheckPortArgument.getValue());
    }

    @Test
    void testDeleteEvidence_IdGivenButEvidenceNotExist_ErrorMessage() {
        UUID id = UUID.randomUUID();
        when(checkEvidenceExistencePort.existsById(id)).thenReturn(Boolean.FALSE);
        var param = new DeleteEvidenceUseCase.Param(id);
        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.deleteEvidence(param));
        assertThat(throwable).hasMessage(DELETE_EVIDENCE_EVIDENCE_NOT_FOUND);
    }
}
