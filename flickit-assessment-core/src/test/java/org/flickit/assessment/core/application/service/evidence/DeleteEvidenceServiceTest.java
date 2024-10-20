package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.DeleteEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;
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
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testDeleteEvidence_IdGiven_Delete() {
        Evidence evidence = EvidenceMother.simpleEvidence();
        UUID evidenceId = evidence.getId();
        UUID currentUserId = evidence.getCreatedById();
        doNothing().when(deleteEvidencePort).deleteById(evidenceId);
        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, DELETE_EVIDENCE)).thenReturn(true);
        service.deleteEvidence(new DeleteEvidenceUseCase.Param(evidenceId, currentUserId));

        ArgumentCaptor<UUID> idDeletePortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(deleteEvidencePort).deleteById(idDeletePortArgument.capture());

        assertEquals(evidenceId, idDeletePortArgument.getValue());

        ArgumentCaptor<UUID> idCheckPortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(loadEvidencePort).loadNotDeletedEvidence(idCheckPortArgument.capture());

        assertEquals(evidenceId, idCheckPortArgument.getValue());
    }

    @Test
    void testDeleteEvidence_IdGivenButEvidenceNotExist_ErrorMessage() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        when(loadEvidencePort.loadNotDeletedEvidence(id))
            .thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));
        var param = new DeleteEvidenceUseCase.Param(id, currentUserId);
        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.deleteEvidence(param));
        assertThat(throwable).hasMessage(EVIDENCE_ID_NOT_FOUND);
    }

    @Test
    void testDeleteEvidence_InvalidUser_ThrowsException() {
        Evidence evidence = EvidenceMother.simpleEvidence();
        UUID evidenceId = evidence.getId();
        UUID currentUserId = UUID.randomUUID();
        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);

        DeleteEvidenceUseCase.Param param = new DeleteEvidenceUseCase.Param(evidenceId, currentUserId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteEvidence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(deleteEvidencePort, never()).deleteById(any());

        ArgumentCaptor<UUID> idCheckPortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(loadEvidencePort).loadNotDeletedEvidence(idCheckPortArgument.capture());

        assertEquals(evidenceId, idCheckPortArgument.getValue());
    }

    @Test
    void testDeleteEvidence_NotAuthorizedUser_ThrowsException() {
        Evidence evidence = EvidenceMother.simpleEvidence();
        UUID evidenceId = evidence.getId();
        UUID currentUserId = evidence.getCreatedById();
        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, DELETE_EVIDENCE)).thenReturn(false);

        DeleteEvidenceUseCase.Param param = new DeleteEvidenceUseCase.Param(evidenceId, currentUserId);
        assertThrows(AccessDeniedException.class, () -> service.deleteEvidence(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(deleteEvidencePort, never()).deleteById(any());

        ArgumentCaptor<UUID> idCheckPortArgument = ArgumentCaptor.forClass(UUID.class);
        verify(loadEvidencePort).loadNotDeletedEvidence(idCheckPortArgument.capture());

        assertEquals(evidenceId, idCheckPortArgument.getValue());
    }
}
