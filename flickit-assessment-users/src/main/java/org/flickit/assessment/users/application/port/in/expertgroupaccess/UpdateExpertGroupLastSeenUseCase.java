package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_EXPERT_GROUP_LAST_SEEN_EXPERT_GROUP_ID_NOT_NULL;

public interface UpdateExpertGroupLastSeenUseCase {

    void updateLastSeen(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_EXPERT_GROUP_LAST_SEEN_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long expertGroupId, UUID currentUserId) {
            this.expertGroupId = expertGroupId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
