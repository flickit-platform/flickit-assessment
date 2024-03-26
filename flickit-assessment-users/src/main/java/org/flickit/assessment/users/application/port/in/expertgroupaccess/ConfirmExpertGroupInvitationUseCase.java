package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_NOT_NULL;

public interface ConfirmExpertGroupInvitationUseCase {

    void confirmInvitation(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<InviteExpertGroupMemberUseCase.Param> {

        @NotNull(message = CONFIRM_EXPERT_GROUP_INVITATION_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_NOT_NULL)
        UUID inviteToken;

        public Param(Long expertGroupId, UUID userId, UUID inviteToken) {
            this.expertGroupId = expertGroupId;
            this.inviteToken = inviteToken;
            this.userId = userId;
            this.validateSelf();
        }
    }
}
