package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_SPACE_SPACE_ID_NOT_NULL;

public interface LeaveSpaceUseCase {

    void leaveSpace(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = LEAVE_SPACE_SPACE_ID_NOT_NULL)
        Long id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long spaceId, UUID currentUserId) {
            this.id = spaceId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
