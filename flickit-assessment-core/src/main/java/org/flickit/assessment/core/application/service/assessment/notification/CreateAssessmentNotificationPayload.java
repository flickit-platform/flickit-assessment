package org.flickit.assessment.core.application.service.assessment.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

public record CreateAssessmentNotificationPayload(KitModel kit) implements NotificationPayload {

    public record KitModel(long id, String title) {}
}
