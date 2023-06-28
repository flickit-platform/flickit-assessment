package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.evidence.EditEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.SaveEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.EDIT_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditEvidenceServiceTest {

    public static final String DESC = "description";
    public static final String NEW_DESC = "new_description";
    private final LoadEvidencePort loadEvidence = Mockito.mock(LoadEvidencePort.class);
    private final SaveEvidencePort saveEvidence = Mockito.mock(SaveEvidencePort.class);

    private final EditEvidenceService service = new EditEvidenceService(
        loadEvidence,
        saveEvidence
    );

    Evidence savedEvidence = new Evidence(
        UUID.randomUUID(),
        DESC,
        LocalDateTime.now(),
        LocalDateTime.now(),
        1L,
        UUID.randomUUID(),
        1L
    );

    @Test
    void editEvidence_InputsValidAndIdFound_EditedAndSavedEvidence() {
        when(loadEvidence.loadEvidence(new LoadEvidencePort.Param(savedEvidence.getId()))).thenReturn(new LoadEvidencePort.Result(savedEvidence));
        when(saveEvidence.saveEvidence(any(SaveEvidencePort.Param.class))).thenReturn(new SaveEvidencePort.Result(savedEvidence.getId()));

        EditEvidenceUseCase.Result result = service.editEvidence(new EditEvidenceUseCase.Param(
            savedEvidence.getId(),
            NEW_DESC
        ));

        assertEquals(savedEvidence.getId(), result.id());
    }

    @Test
    void editEvidence_InputsValidAndIdNotFound_NotFoundException() {
        when(loadEvidence.loadEvidence(new LoadEvidencePort.Param(savedEvidence.getId()))).thenReturn(new LoadEvidencePort.Result(null));

        assertThrows(ResourceNotFoundException.class,
            () -> service.editEvidence(new EditEvidenceUseCase.Param(
                savedEvidence.getId(),
                NEW_DESC
            )));
    }

    @Test
    void editEvidence_EmptyId_ErrorMessage() {
        when(loadEvidence.loadEvidence(new LoadEvidencePort.Param(savedEvidence.getId()))).thenReturn(new LoadEvidencePort.Result(savedEvidence));

        assertThrows(ConstraintViolationException.class,
            () -> service.editEvidence(new EditEvidenceUseCase.Param(
                null,
                NEW_DESC
            )),
            EDIT_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    @Disabled
    void editEvidence_DescriptionIsEmpty_SavedEvidence() {
        when(loadEvidence.loadEvidence(any(LoadEvidencePort.Param.class))).thenReturn(new LoadEvidencePort.Result(savedEvidence));

        EditEvidenceUseCase.Result result = service.editEvidence(new EditEvidenceUseCase.Param(
            savedEvidence.getId(),
            ""
        ));

        assertEquals(savedEvidence.getId(), result.id());
    }


}
