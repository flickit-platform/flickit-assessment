package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.kit.common.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GrantUserAccessToKitUseCase {

    void grantUserAccessToKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<UpdateKitByDslUseCase.Param> {

        @NotNull(message = GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotBlank(message = GRANT_USER_ACCESS_TO_KIT_USER_EMAIL_NOT_NULL)
        String userEmail;

        @NotNull(message = GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_NOT_NULL)
        UUID currentUser;

        public Param(Long kitId, String userEmail, UUID currentUser) {
            this.kitId = kitId;
            this.userEmail = userEmail;
            this.currentUser = currentUser;
            this.validateSelf();
        }
    }
}
