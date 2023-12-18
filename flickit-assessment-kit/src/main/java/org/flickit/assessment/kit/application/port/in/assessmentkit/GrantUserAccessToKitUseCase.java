package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GrantUserAccessToKitUseCase {

    void grantUserAccessToKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<UpdateKitByDslUseCase.Param> {

        @NotNull(message = GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotBlank(message = GRANT_USER_ACCESS_TO_KIT_EMAIL_NOT_NULL)
        String email;

        @NotNull(message = GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, String email, UUID currentUserId) {
            this.kitId = kitId;
            this.email = email;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
