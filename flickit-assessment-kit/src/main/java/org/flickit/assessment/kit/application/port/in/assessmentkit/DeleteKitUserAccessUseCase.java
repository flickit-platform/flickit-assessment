package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_EMAIL_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL;

public interface DeleteKitUserAccessUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<UpdateKitByDslUseCase.Param> {

        @NotNull(message = DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL)
        Long kitId;

        @NotBlank(message = DELETE_KIT_USER_ACCESS_EMAIL_NOT_NULL)
        String email;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, String email, UUID currentUserId) {
            this.kitId = kitId;
            this.email = email;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
