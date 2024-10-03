package org.flickit.assessment.core.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.List;
import java.util.UUID;

public record AcceptAssessmentInvitationNotificationsCmd(
    List<NotificationCmdItem> notificationCmdItems) implements NotificationCmd {

    public record NotificationCmdItem(UUID targetUserId, UUID inviteeId) implements NotificationCmd {
    }
}
