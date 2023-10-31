package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteEvidenceUseCaseParamTest {

    @Test
    void testDeleteEvidence_IdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceUseCase.Param(null));
        assertThat(throwable).hasMessage("id: " + DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL);
    }
}
