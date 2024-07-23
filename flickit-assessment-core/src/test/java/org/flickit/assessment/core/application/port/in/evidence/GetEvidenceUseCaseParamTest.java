package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetEvidenceUseCaseParamTest {

    @Test
    void testGetEvidenceParam_IdIsNull_ReturnErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("id: " + GET_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testGetEvidenceParam_CurrentUserIdIsNull_ReturnErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceUseCase.Param(id, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
