package org.flickit.assessment.core.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

public record CreateAssessmentNotificationCmd(long kitId, UUID creatorId) implements NotificationCmd {}
