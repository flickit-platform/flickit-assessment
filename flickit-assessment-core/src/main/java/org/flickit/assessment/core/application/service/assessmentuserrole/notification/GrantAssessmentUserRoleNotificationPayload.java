package org.flickit.assessment.core.application.service.assessmentuserrole.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.util.UUID;

public record GrantAssessmentUserRoleNotificationPayload(AssessmentModel assessment,
                                                         UserModel assigner,
                                                         RoleModel role) implements NotificationPayload {

    public record AssessmentModel(UUID id, String title) {
    }

    public record UserModel(UUID id, String displayName) {
    }

    public record RoleModel(String title) {
    }
}
