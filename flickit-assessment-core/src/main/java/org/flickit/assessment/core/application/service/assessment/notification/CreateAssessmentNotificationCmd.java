package org.flickit.assessment.core.application.service.assessment.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

public record CreateAssessmentNotificationCmd(long kitId) implements NotificationCmd {}
