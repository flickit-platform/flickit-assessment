package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceUseCaseParamTest {

    @Test
    void testUpdateEvidenceParam_IdIsEmpty_ErrorMessage() {
        UUID lastModifiedById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(null,"new_description", lastModifiedById));
        assertThat(throwable).hasMessage("id: " + UPDATE_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionIsBlank_ErrorMessage() {
        var id = UUID.randomUUID();
        UUID lastModifiedById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id,"    ", lastModifiedById));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_NOT_BLANK);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionIsLessThanMin_ErrorMessage() {
        var id = UUID.randomUUID();
        UUID lastModifiedById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id,"ab", lastModifiedById));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MIN_SIZE);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionSizeIsEqualToMin_Success() {
        assertDoesNotThrow(() -> new UpdateEvidenceUseCase.Param(UUID.randomUUID(),"abc", UUID.randomUUID()));
    }

    @Test
    void testUpdateEvidenceParam_DescriptionSizeIsGreaterThanMax_ErrorMessage() {
        var id = UUID.randomUUID();
        var desc = randomAlphabetic(1001);
        UUID lastModifiedById = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id, desc, lastModifiedById));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MAX_SIZE);
    }

    @Test
    void testUpdateEvidenceParam_DescriptionSizeIsEqualToMax_Success() {
        assertDoesNotThrow(() -> new UpdateEvidenceUseCase.Param(
            UUID.randomUUID(),
            randomAlphabetic(1000),
            UUID.randomUUID()
        ));
    }

    @Test
    void testUpdateEvidenceParam_lastModifiedByIdIsEmpty_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(id,"new_description", null));
        assertThat(throwable).hasMessage("lastModifiedById: " + UPDATE_EVIDENCE_LAST_MODIFIED_BY_ID_NOT_NULL);
    }
}
