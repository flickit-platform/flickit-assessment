package org.flickit.assessment.users.application.service.space.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.time.LocalDateTime;

public record CreatePremiumSpaceNotificationPayload(UserModel userModel,
                                                    SpaceModel spaceModel) implements NotificationPayload {

    public record UserModel(String displayName, String email) {
    }

    public record SpaceModel(String title, LocalDateTime createdAt) {
    }
}
