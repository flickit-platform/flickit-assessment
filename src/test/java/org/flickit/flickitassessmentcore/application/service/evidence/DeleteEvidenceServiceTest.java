package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.domain.mother.EvidenceMother;
import org.flickit.flickitassessmentcore.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CheckEvidenceExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.DeleteEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteEvidenceServiceTest {

    @InjectMocks
    private DeleteEvidenceService service;
    @Mock
    private DeleteEvidencePort deleteEvidencePort;
    @Mock
    private CheckEvidenceExistencePort checkEvidenceExistencePort;

    @Test
    void deleteEvidence_IdGiven_Delete() {
        var evidence = EvidenceMother.deletedEvidence();
        doNothing().when(deleteEvidencePort).setDeletionTimeById(eq(evidence.getId()), any());
        when(checkEvidenceExistencePort.existsById(evidence.getId())).thenReturn(Boolean.TRUE);
        service.deleteEvidence(new DeleteEvidenceUseCase.Param(evidence.getId()));

        ArgumentCaptor<UUID> idDeletePortArgument = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Long> deletionTimeDeletePortArgument = ArgumentCaptor.forClass(Long.class);
        verify(deleteEvidencePort).setDeletionTimeById(idDeletePortArgument.capture(),
            deletionTimeDeletePortArgument.capture());

        assertEquals(evidence.getId(), idDeletePortArgument.getValue());

        ArgumentCaptor<UUID> idCheckPortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(checkEvidenceExistencePort).existsById(idCheckPortArgument.capture());

        assertEquals(evidence.getId(), idCheckPortArgument.getValue());
    }

    @Test
    void deleteEvidence_IdGivenButEvidenceNotExist_ErrorMessage() {
        UUID id = UUID.randomUUID();
        when(checkEvidenceExistencePort.existsById(id)).thenReturn(Boolean.FALSE);
        var param = new DeleteEvidenceUseCase.Param(id);
        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.deleteEvidence(param));
        assertThat(throwable).hasMessage(DELETE_EVIDENCE_EVIDENCE_NOT_FOUND);
    }
}
