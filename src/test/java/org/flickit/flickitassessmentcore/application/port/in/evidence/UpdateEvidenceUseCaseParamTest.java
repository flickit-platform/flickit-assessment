package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceUseCaseParamTest {

    @Test
    void testUpdateEvidenceParam_IdIsEmpty_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(null,"new_description"));
        assertThat(throwable).hasMessage("id: " + UPDATE_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionIsBlank_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id,"    "));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_NOT_BLANK);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionIsLessThanMin_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id,"ab"));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MIN_SIZE);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionSizeIsEqualToMin_Success() {
        assertDoesNotThrow(() -> new UpdateEvidenceUseCase.Param(UUID.randomUUID(),"abc"));
    }

    @Test
    void testUpdateEvidenceParam_DescriptionSizeIsGreaterThanMax_ErrorMessage() {
        var id = UUID.randomUUID();
        var desc = randomAlphabetic(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id, desc));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MAX_SIZE);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionSizeIsEqualToMax_Success() {
        assertDoesNotThrow(() -> new UpdateEvidenceUseCase.Param(
            UUID.randomUUID(),
            randomAlphabetic(1000)
        ));
    }
}
