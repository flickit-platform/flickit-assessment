package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteEvidenceUseCaseParamTest {

    @Test
    void testDeleteEvidence_IdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("id: " + DELETE_EVIDENCE_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testDeleteEvidence_CurrentUserIdIsNull_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceUseCase.Param(id, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
