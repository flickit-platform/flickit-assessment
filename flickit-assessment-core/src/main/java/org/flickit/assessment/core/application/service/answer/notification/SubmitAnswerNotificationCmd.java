package org.flickit.assessment.core.application.service.answer.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

public record SubmitAnswerNotificationCmd(UUID targetUserId,
                                          UUID assessmentId,
                                          UUID assessorId) implements NotificationCmd {
}
