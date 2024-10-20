package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.UpdateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.EvidenceMother.simpleEvidence;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceServiceTest {

    @InjectMocks
    private UpdateEvidenceService service;

    @Mock
    private UpdateEvidencePort updateEvidencePort;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testUpdateEvidence_ValidParam_UpdatedAndReturnsId() {
        var savedEvidence = simpleEvidence();
        var code = EvidenceType.values()[savedEvidence.getType()].getCode();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription(),
            code,
            savedEvidence.getCreatedById()
        );

        var updateResult = new UpdateEvidencePort.Result(savedEvidence.getId());

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(savedEvidence);
        when(assessmentAccessChecker.isAuthorized(savedEvidence.getAssessmentId(), param.getCurrentUserId(), UPDATE_EVIDENCE)).thenReturn(true);
        when(updateEvidencePort.update(any())).thenReturn(updateResult);

        UpdateEvidenceUseCase.Result result = service.updateEvidence(param);

        assertEquals(savedEvidence.getId(), result.id());

        ArgumentCaptor<UpdateEvidencePort.Param> updateParamArgumentCaptor = ArgumentCaptor.forClass(UpdateEvidencePort.Param.class);
        verify(updateEvidencePort).update(updateParamArgumentCaptor.capture());

        assertEquals(param.getId(), updateParamArgumentCaptor.getValue().id());
        assertEquals(param.getDescription(), updateParamArgumentCaptor.getValue().description());
        assertNotEquals(savedEvidence.getLastModificationTime(), updateParamArgumentCaptor.getValue().lastModificationTime());
    }

    @Test
    void testUpdateEvidence_InvalidUser_ThrowsException() {
        var savedEvidence = simpleEvidence();
        var code = EvidenceType.values()[savedEvidence.getType()].getCode();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription(),
            code,
            UUID.randomUUID()
        );

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(savedEvidence);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateEvidence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateEvidence_NotAuthorizedUser_ThrowsException() {
        var savedEvidence = simpleEvidence();
        var code = EvidenceType.values()[savedEvidence.getType()].getCode();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription(),
            code,
            savedEvidence.getCreatedById()
        );

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(savedEvidence);
        when(assessmentAccessChecker.isAuthorized(savedEvidence.getAssessmentId(), param.getCurrentUserId(), UPDATE_EVIDENCE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateEvidence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateEvidence_InvalidEvidenceId_ThrowsException() {
        var code = EvidenceType.values()[0].getCode();
        var param = new UpdateEvidenceUseCase.Param(
            UUID.randomUUID(),
            "new Desc",
            code,
            UUID.randomUUID()
        );

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId()))
            .thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.updateEvidence(param));
        assertEquals(EVIDENCE_ID_NOT_FOUND, exception.getMessage());
    }
}
