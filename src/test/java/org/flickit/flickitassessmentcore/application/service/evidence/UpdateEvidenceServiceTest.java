package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistenceByEvidenceIdPort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private CheckAssessmentExistenceByEvidenceIdPort checkAssessmentExistencePort;

    @Test
    void updateEvidence_ValidParam_UpdatedAndReturnsId() {
        var savedEvidence = simpleEvidence();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription()
        );

        var updateResult = new UpdateEvidencePort.Result(savedEvidence.getId());
        when(checkAssessmentExistencePort.isAssessmentExistsByEvidenceId(any())).thenReturn(true);
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
    void updateEvidence_InvalidAssessmentId_ThrowNotFoundException() {
        var savedEvidence = simpleEvidence();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription()
        );

        when(checkAssessmentExistencePort.isAssessmentExistsByEvidenceId(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.updateEvidence(param));

        ArgumentCaptor<UUID> evidenceIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(checkAssessmentExistencePort).isAssessmentExistsByEvidenceId(evidenceIdArgumentCaptor.capture());

        assertEquals(param.getId(), evidenceIdArgumentCaptor.getValue());
        verify(checkAssessmentExistencePort, times(1)).isAssessmentExistsByEvidenceId(any());
        verify(updateEvidencePort, never()).update(any());
    }

}
