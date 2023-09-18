package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.flickit.flickitassessmentcore.application.domain.mother.EvidenceMother.simpleEvidence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceServiceTest {

    public static final String NEW_DESC = "new_description";
    @InjectMocks
    private UpdateEvidenceService service;

    @Mock
    private UpdateEvidencePort updateEvidencePort;

    @Test
    void updateEvidence_InputsValidAndIdFound_EditedAndSavedEvidence() {
        var savedEvidence = simpleEvidence();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            NEW_DESC
        );
        var updateParam = new UpdateEvidencePort.Param(
            param.getId(),
            param.getDescription(),
            LocalDateTime.now()
        );
        var updateResult = new UpdateEvidencePort.Result(savedEvidence.getId());
        when(updateEvidencePort.update(any())).thenReturn(updateResult);

        UpdateEvidenceUseCase.Result result = service.updateEvidence(param);

        assertEquals(savedEvidence.getId(), result.id());

        ArgumentCaptor<UpdateEvidencePort.Param> updateParamArgumentCaptor = ArgumentCaptor.forClass(UpdateEvidencePort.Param.class);
        verify(updateEvidencePort).update(updateParamArgumentCaptor.capture());

        assertEquals(savedEvidence.getId(), updateParamArgumentCaptor.getValue().id());
        assertEquals(NEW_DESC, updateParamArgumentCaptor.getValue().description());
        assertNotEquals(savedEvidence.getLastModificationTime(), updateParamArgumentCaptor.getValue().lastModificationTime());
    }

}
