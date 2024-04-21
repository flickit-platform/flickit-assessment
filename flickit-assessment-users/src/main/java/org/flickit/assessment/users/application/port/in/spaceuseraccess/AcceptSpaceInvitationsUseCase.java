package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface AcceptSpaceInvitationsUseCase {

    void acceptInvitations (Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = ACCEPT_SPACE_INVITATIONS_EMAIL_NOT_NULL)
        String email;

        public Param(UUID userId, String email) {
            this.userId = userId;
            this.email = email;
            this.validateSelf();
        }
    }
}
