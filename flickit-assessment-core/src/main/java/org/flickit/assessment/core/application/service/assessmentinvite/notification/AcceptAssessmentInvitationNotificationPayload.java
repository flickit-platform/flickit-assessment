package org.flickit.assessment.core.application.service.assessmentinvite.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.util.UUID;

public record AcceptAssessmentInvitationNotificationPayload(InviteeModel inviteeModel) implements NotificationPayload {

    public record InviteeModel(UUID id, String displayName) {
    }
}
