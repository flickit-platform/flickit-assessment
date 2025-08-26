package org.flickit.assessment.kit.application.service.assessmentkit.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

public record ToggleKitLikeNotificationPayload(AssessmentKitModel kit,
                                               UserModel liker) implements NotificationPayload {

    public record AssessmentKitModel(Long id, String title) {
    }

    public record UserModel(String displayName) {
    }
}
