package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL;

public interface AcceptSpaceInvitationsUseCase {

    void acceptInvitations (Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL)
        UUID userId;

        public Param(UUID userId) {
            this.userId = userId;
            this.validateSelf();
        }
    }
}
