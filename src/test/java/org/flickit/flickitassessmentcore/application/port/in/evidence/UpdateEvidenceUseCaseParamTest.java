package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceUseCaseParamTest {

    @Test
    void updateEvidence_EmptyId_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(
                null,
                "new_description"
            ));
        assertThat(throwable).hasMessage("id: " + UPDATE_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void updateEvidence_DescriptionIsEmpty_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(
                id,
                "    "
            ));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_NOT_BLANK);
    }

    @Test
    void updateEvidence_DescriptionIsUnderMinSize_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(
                id,
                "de"
            ));
        assertThat(throwable).hasMessage("description: "+ UPDATE_EVIDENCE_DESC_MIN_SIZE);
    }

    @Test
    void updateEvidence_DescriptionIsAboveMaxSize_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(
                id,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum elit eu interdum. Lorem ipsum "
            ));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MAX_SIZE);
    }
}
