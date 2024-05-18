package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_SPACE_LAST_SEEN_SPACE_ID_NOT_NULL;

public interface UpdateSpaceLastSeenUseCase {

    void updateLastSeen(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_SPACE_LAST_SEEN_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long spaceId, UUID currentUserId) {
            this.spaceId = spaceId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
