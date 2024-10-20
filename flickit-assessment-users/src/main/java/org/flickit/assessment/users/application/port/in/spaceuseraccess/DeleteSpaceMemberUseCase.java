package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_USER_ID_NOT_NULL;

public interface DeleteSpaceMemberUseCase {

    void deleteMember(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_SPACE_MEMBER_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotNull(message = DELETE_SPACE_MEMBER_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long spaceId, UUID userId, UUID currentUserId) {
            this.spaceId = spaceId;
            this.userId = userId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
