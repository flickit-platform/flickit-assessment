package org.flickit.assessment.users.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;
import org.flickit.assessment.users.application.domain.Space;

public record CreatePremiumSpaceNotificationCmd(String adminEmail, Space space) implements NotificationCmd {
}
