package org.flickit.assessment.common.application.domain.notification;

import java.util.UUID;

public interface SendNotificationPort {

    void send(UUID targetUserId, NotificationContent content);
}
