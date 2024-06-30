package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEvidenceServiceTest {

    @InjectMocks
    private GetEvidenceService service;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    @DisplayName("For loading an evidence, the evidence should be exist or throw notFoundException.")
    void testLoadEvidence_evidenceNotExist_NotFoundException() {
        var id = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(id)).thenThrow(new ResourceNotFoundException(GET_EVIDENCE_ID_NOT_NULL));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getEvidence(param));

        assertEquals(GET_EVIDENCE_ID_NOT_NULL, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(id);
        verifyNoInteractions(checkUserAssessmentAccessPort);
    }

    @Test
    @DisplayName("For loading an evidence, the current user should have access to the corresponding kit.")
    void testLoadEvidence_AccessDoesNotExist_AccessDeniedException() {
        var id = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);
        var evidence = EvidenceMother.simpleEvidence();

        when(loadEvidencePort.loadNotDeletedEvidence(id)).thenReturn(evidence);
        when(checkUserAssessmentAccessPort.hasAccess(evidence.getAssessmentId(), currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getEvidence(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(id);
        verify(checkUserAssessmentAccessPort).hasAccess(evidence.getAssessmentId(), currentUserId);
    }
}
