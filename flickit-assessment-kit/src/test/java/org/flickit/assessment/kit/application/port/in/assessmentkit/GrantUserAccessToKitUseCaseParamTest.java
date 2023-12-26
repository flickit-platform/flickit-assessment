package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrantUserAccessToKitUseCaseParamTest {

    @Test
    void testGrantUserAccessToKitParam_kitIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAccessToKitUseCase.Param(null, userId, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAccessToKitParam_userIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAccessToKitUseCase.Param(1L, null, currentUserId));
        assertThat(throwable).hasMessage("userId: " + GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAccessToKitParam_currentUserIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAccessToKitUseCase.Param(1L, userId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
