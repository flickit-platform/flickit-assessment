package org.flickit.assessment.core.application.domain.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.Set;
import java.util.UUID;

public record AcceptAssessmentInvitationNotificationsCmd(Set<UUID> targetUserIds,
                                                         UUID inviteeId) implements NotificationCmd {
}
