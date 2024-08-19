package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.notification.HasNotificationCmd;
import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GrantUserAssessmentRoleUseCase {

    Result grantAssessmentUserRole(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GRANT_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_NULL)
        Integer roleId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, UUID userId, Integer roleId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.userId = userId;
            this.roleId = roleId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(NotificationCmd notificationCmd) implements HasNotificationCmd {

    }
}
