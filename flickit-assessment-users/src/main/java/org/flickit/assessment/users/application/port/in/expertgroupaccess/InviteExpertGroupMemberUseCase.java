package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL;

public interface InviteExpertGroupMemberUseCase {

    void inviteMember(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long expertGroupId, UUID userId, UUID currentUserId) {
            this.expertGroupId = expertGroupId;
            this.userId = userId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
