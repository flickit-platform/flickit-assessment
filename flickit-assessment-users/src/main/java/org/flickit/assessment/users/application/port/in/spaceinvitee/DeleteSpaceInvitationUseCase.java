package org.flickit.assessment.users.application.port.in.spaceinvitee;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_INVITE_ID_NOT_NULL;

public interface DeleteSpaceInvitationUseCase {

    void deleteInvitation(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_SPACE_INVITATION_INVITE_ID_NOT_NULL)
        UUID inviteId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID inviteId, UUID currentUserId) {
            this.inviteId = inviteId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
