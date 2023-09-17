package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.port.in.evidence.EditEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.SaveEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.application.domain.mother.EvidenceMother.simpleEvidence;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.EDIT_EVIDENCE_EVIDENCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditEvidenceServiceTest {

    public static final String NEW_DESC = "new_description";
    @InjectMocks
    private EditEvidenceService service;
    @Mock
    private LoadEvidencePort loadEvidencePort;
    @Mock
    private SaveEvidencePort saveEvidencePort;

    @Test
    void editEvidence_InputsValidAndIdFound_EditedAndSavedEvidence() {
        var savedEvidence = simpleEvidence();
        when(loadEvidencePort.loadEvidence(savedEvidence.getId())).thenReturn(savedEvidence);
        when(saveEvidencePort.saveEvidence(any())).thenReturn(savedEvidence.getId());

        EditEvidenceUseCase.Result result = service.editEvidence(new EditEvidenceUseCase.Param(
            savedEvidence.getId(),
            NEW_DESC
        ));

        assertEquals(savedEvidence.getId(), result.id());

        ArgumentCaptor<Evidence> evidenceArgumentCaptor = ArgumentCaptor.forClass(Evidence.class);
        verify(saveEvidencePort).saveEvidence(evidenceArgumentCaptor.capture());

        assertEquals(savedEvidence.getId(), evidenceArgumentCaptor.getValue().getId());
        assertEquals(NEW_DESC, evidenceArgumentCaptor.getValue().getDescription());
        assertNotEquals(savedEvidence.getLastModificationTime(), evidenceArgumentCaptor.getValue().getLastModificationTime());
    }

    @Test
    void editEvidence_InputsValidAndIdNotFound_NotFoundException() {
        var savedEvidence = simpleEvidence();
        when(loadEvidencePort.loadEvidence(savedEvidence.getId()))
            .thenThrow(new ResourceNotFoundException(EDIT_EVIDENCE_EVIDENCE_NOT_FOUND));

        var param = new EditEvidenceUseCase.Param(
            savedEvidence.getId(),
            NEW_DESC
        );

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.editEvidence(param));
        assertThat(throwable).hasMessage(EDIT_EVIDENCE_EVIDENCE_NOT_FOUND);
    }

}
