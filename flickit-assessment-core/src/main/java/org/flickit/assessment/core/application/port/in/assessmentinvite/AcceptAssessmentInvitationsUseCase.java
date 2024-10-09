package org.flickit.assessment.core.application.port.in.assessmentinvite;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.notification.HasNotificationCmd;
import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_NULL;

public interface AcceptAssessmentInvitationsUseCase {

    Result acceptInvitations(Param param);

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

    record Result(NotificationCmd notificationCmd) implements HasNotificationCmd {
    }
}
