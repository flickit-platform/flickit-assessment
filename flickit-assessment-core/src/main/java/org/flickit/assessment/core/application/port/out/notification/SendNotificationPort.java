package org.flickit.assessment.core.application.port.out.notification;

import org.flickit.assessment.common.application.domain.assessment.NotificationType;

import java.util.UUID;

public interface SendNotificationPort {

    void sendNotification(UUID userId, NotificationType type, Object data);
}
