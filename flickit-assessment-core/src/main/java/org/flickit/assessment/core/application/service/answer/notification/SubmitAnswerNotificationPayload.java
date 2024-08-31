package org.flickit.assessment.core.application.service.answer.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.util.UUID;

public record SubmitAnswerNotificationPayload(AssessmentModel assessment,
                                              UserModel assessor) implements NotificationPayload {

    public record AssessmentModel(UUID id, String title) {
    }

    public record UserModel(UUID id, String displayName) {
    }
}
