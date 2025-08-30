package org.flickit.assessment.kit.application.service.assessmentkit.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

public record ToggleKitLikeNotificationCmd(Long kitId, UUID likerId) implements NotificationCmd {
}
