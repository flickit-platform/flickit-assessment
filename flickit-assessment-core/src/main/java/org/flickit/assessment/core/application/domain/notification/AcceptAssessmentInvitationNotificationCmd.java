package org.flickit.assessment.core.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.util.List;
import java.util.UUID;

public record AcceptAssessmentInvitationNotificationCmd(
    List<NotificationCmdItem> notificationCmdItems) implements NotificationCmd {

    public record NotificationCmdItem(UUID targetUserId,
                                      UUID assessmentId,
                                      UUID inviteeId,
                                      AssessmentUserRole assessmentUserRole) implements NotificationCmd {
    }
}
