package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.TOGGLE_KIT_LIKE_KIT_ID_NOT_NULL;

public interface ToggleKitLikeUseCase {

    Result toggleKitLike(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = TOGGLE_KIT_LIKE_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, UUID currentUserId) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(int count, boolean liked) {
    }
}
