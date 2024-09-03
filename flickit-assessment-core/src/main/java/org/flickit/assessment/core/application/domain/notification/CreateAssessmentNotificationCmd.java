package org.flickit.assessment.core.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

public record CreateAssessmentNotificationCmd(long kitId) implements NotificationCmd {}
