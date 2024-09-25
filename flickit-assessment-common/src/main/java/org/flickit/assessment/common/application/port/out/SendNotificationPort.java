package org.flickit.assessment.common.application.port.out;

import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;

public interface SendNotificationPort {

    void send(NotificationEnvelope envelope);
}
