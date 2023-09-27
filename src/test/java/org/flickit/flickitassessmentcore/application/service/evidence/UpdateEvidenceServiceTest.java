package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.GetEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.domain.mother.EvidenceMother.simpleEvidence;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceServiceTest {

    @InjectMocks
    private UpdateEvidenceService service;

    @Mock
    private UpdateEvidencePort updateEvidencePort;

    @Mock
    private GetEvidencePort getEvidencePort;
    @Mock
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Test
    void testUpdateEvidence_ValidParam_UpdatedAndReturnsId() {
        var savedEvidence = simpleEvidence();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription()
        );

        var updateResult = new UpdateEvidencePort.Result(savedEvidence.getId());
        when(getEvidencePort.getEvidenceById(any())).thenReturn(Optional.of(savedEvidence));
        when(checkAssessmentExistencePort.existsById(any())).thenReturn(true);
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
    void updateEvidence_InvalidEvidenceId_ThrowNotFoundException() {
        var savedEvidence = simpleEvidence();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription()
        );
        when(getEvidencePort.getEvidenceById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateEvidence(param));

        ArgumentCaptor<UUID> evidenceIdParam = ArgumentCaptor.forClass(UUID.class);
        verify(getEvidencePort).getEvidenceById(evidenceIdParam.capture());
        assertEquals(param.getId(), evidenceIdParam.getValue());

        verify(getEvidencePort, times(1)).getEvidenceById(any());
        verify(checkAssessmentExistencePort, never()).existsById(any());
        verify(updateEvidencePort, never()).update(any());
    }

    @Test
    void updateEvidence_DeletedAssessment_ThrowNotFoundException() {
        var savedEvidence = simpleEvidence();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription()
        );
        when(getEvidencePort.getEvidenceById(any())).thenReturn(Optional.of(savedEvidence));
        when(checkAssessmentExistencePort.existsById(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.updateEvidence(param));

        ArgumentCaptor<UUID> assessmentIdParam = ArgumentCaptor.forClass(UUID.class);
        verify(checkAssessmentExistencePort).existsById(assessmentIdParam.capture());
        assertEquals(savedEvidence.getAssessmentId(), assessmentIdParam.getValue());

        verify(getEvidencePort, times(1)).getEvidenceById(any());
        verify(checkAssessmentExistencePort, times(1)).existsById(any());
        verify(updateEvidencePort, never()).update(any());
    }

}
