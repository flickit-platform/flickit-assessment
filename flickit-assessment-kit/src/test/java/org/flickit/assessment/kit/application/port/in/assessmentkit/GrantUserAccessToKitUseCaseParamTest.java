package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrantUserAccessToKitUseCaseParamTest {

    @Test
    void testGrantUserAccessToKitParam_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAccessToKitUseCase.Param(null, "ex@email.com", currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAccessToKitParam_emailIsBlank_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAccessToKitUseCase.Param(1L, "  ", currentUserId));
        assertThat(throwable).hasMessage("email: " + GRANT_USER_ACCESS_TO_KIT_EMAIL_NOT_NULL);
    }

    @Test
    void testGrantUserAccessToKitParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAccessToKitUseCase.Param(1L, "ex@email.com", null));
        assertThat(throwable).hasMessage("currentUserId: " + GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_ID_NOT_NULL);
    }
}
