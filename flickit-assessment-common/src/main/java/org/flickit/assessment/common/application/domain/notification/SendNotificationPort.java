package org.flickit.assessment.common.application.domain.notification;

public interface SendNotificationPort {

    void send(NotificationEnvelope envelope);
}
