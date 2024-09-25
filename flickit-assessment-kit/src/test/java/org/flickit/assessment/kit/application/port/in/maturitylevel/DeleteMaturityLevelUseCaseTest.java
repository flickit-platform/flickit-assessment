package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_MATURITY_LEVEL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteMaturityLevelUseCaseTest {

    @Test
    void testDeleteMaturityLevelParam_maturityLevelIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteMaturityLevelUseCase.Param(null, 123L, currentUserId));
        assertThat(throwable).hasMessage("maturityLevelId: " + DELETE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testDeleteMaturityLevelParam_kitIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteMaturityLevelUseCase.Param(123L, null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + DELETE_MATURITY_LEVEL_KIT_ID_NOT_NULL);
    }


    @Test
    void testDeleteMaturityLevelParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteMaturityLevelUseCase.Param(123L, 123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testDeleteMaturityLevelParam_ValidParams_ShouldNotThrowException() {
        var currentUserId = UUID.randomUUID();
        assertDoesNotThrow(() -> new DeleteMaturityLevelUseCase.Param(123L, 123L, currentUserId));
    }
}
