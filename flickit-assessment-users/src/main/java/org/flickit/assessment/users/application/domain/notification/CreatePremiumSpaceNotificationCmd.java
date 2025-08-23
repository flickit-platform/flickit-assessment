package org.flickit.assessment.users.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePremiumSpaceNotificationCmd(String adminEmail,
                                                String title,
                                                UUID createdBy,
                                                LocalDateTime creationTime) implements NotificationCmd {
}
