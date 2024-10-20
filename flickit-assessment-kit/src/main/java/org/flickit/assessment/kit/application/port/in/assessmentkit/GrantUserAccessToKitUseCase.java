package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_NULL;

public interface GrantUserAccessToKitUseCase {

    void grantUserAccessToKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, UUID userId, UUID currentUserId) {
            this.kitId = kitId;
            this.userId = userId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
