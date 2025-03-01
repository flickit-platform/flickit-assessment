package org.flickit.assessment.users.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

public record CreateSpaceNotificationCmd(String adminEmail, long spaceId) implements NotificationCmd {
}
