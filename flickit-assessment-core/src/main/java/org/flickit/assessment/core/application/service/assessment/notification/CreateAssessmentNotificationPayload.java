package org.flickit.assessment.core.application.service.assessment.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.util.UUID;

public record CreateAssessmentNotificationPayload(
        AssessmentModel assessmentModel,
        UserModel assessmentCreator,
        KitModel kitModel,
        SpaceModel spaceModel) implements NotificationPayload {

    public record AssessmentModel(UUID id, String title) {}

    public record UserModel(UUID id, String displayName) {}

    public record KitModel(long id, String title) {}

    public record SpaceModel(long id, String title) {}
}
