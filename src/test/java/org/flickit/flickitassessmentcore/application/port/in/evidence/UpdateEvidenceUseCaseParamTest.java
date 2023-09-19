package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
                "ab"
            ));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MIN_SIZE);
    }

    @Test
    void updateEvidence_DescriptionIsEqualToMinSize() {
        String new_desc = "abc";
        UpdateEvidenceUseCase.Param param = new UpdateEvidenceUseCase.Param(
            UUID.randomUUID(),
            new_desc
        );
        assertEquals(new_desc, param.getDescription());
    }

    @Test
    void updateEvidence_DescriptionIsAboveMaxSize_ErrorMessage() {
        var id = UUID.randomUUID();
        var desc = RandomStringUtils.randomAlphabetic(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateEvidenceUseCase.Param(
                id,
                desc
            ));
        assertThat(throwable).hasMessage("description: " + UPDATE_EVIDENCE_DESC_MAX_SIZE);
    }

    @Test
    void updateEvidence_DescriptionIsEqualToMaxSize_ErrorMessage() {
        String new_desc = RandomStringUtils.randomAlphabetic(1000);
        var param = new UpdateEvidenceUseCase.Param(
            UUID.randomUUID(),
            new_desc
        );
        assertEquals(new_desc, param.getDescription());
    }
}
