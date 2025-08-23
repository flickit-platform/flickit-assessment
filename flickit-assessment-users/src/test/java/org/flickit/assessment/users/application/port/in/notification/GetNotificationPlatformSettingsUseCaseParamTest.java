package org.flickit.assessment.users.application.port.in.notification;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetNotificationPlatformSettingsUseCaseParamTest {

    @Test
    void testGetNotificationPlatformSettingsParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetNotificationPlatformSettingsUseCase.Param(null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
