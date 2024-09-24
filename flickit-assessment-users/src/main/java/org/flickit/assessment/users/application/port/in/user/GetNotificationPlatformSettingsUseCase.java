package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface GetNotificationPlatformSettingsUseCase {

    Result getNotificationPlatformSettings(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID currentUserId) {
            this.currentUserId = currentUserId;
            validateSelf();
        }
    }

    record Result(Map<String, String> settings) {
    }
}
