package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_NULL;

public interface AcceptAssessmentInvitationsUseCase {

    void acceptInvitations (Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_NULL)
        UUID userId;

        public Param(UUID userId) {
            this.userId = userId;
            this.validateSelf();
        }
    }
}
