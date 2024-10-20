package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.TOGGLE_KIT_LIKE_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToggleKitLikeUseCaseParamTest {

    @Test
    void testToggleKitLikeUseCaseParam_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ToggleKitLikeUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + TOGGLE_KIT_LIKE_KIT_ID_NOT_NULL);
    }

    @Test
    void testToggleKitLikeUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ToggleKitLikeUseCase.Param(1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
