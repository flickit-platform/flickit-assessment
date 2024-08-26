package org.flickit.assessment.core.application.service.assessment.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

public record CreateAssessmentNotificationCmd(
    UUID assessmentId,
    UUID assessmentCreatedBy,
    String assessmentTitle,
    long kitId,
    long spaceId) implements NotificationCmd {}
