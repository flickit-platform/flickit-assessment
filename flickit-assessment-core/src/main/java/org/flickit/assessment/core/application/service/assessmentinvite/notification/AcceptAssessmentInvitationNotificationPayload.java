package org.flickit.assessment.core.application.service.assessmentinvite.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.util.UUID;

public record AcceptAssessmentInvitationNotificationPayload(AssessmentModel assessmentModel,
                                                            InviteeModel inviteeModel) implements NotificationPayload {

    public record AssessmentModel(UUID id, String title) {
    }

    public record InviteeModel(UUID id, String displayName) {
    }
}
