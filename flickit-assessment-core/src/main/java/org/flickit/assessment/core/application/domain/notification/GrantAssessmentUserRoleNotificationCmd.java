package org.flickit.assessment.core.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.util.UUID;

public record GrantAssessmentUserRoleNotificationCmd(UUID targetUserId,
                                                     UUID assessmentId,
                                                     UUID assignerUserId,
                                                     AssessmentUserRole role) implements NotificationCmd {

}
