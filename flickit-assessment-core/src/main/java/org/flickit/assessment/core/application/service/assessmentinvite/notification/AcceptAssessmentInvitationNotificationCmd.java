package org.flickit.assessment.core.application.service.assessmentinvite.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.util.UUID;

public record AcceptAssessmentInvitationNotificationCmd(UUID targetUserId,
                                                        UUID assessmentId,
                                                        UUID inviteeUserId,
                                                        AssessmentUserRole assessmentUserRole) implements NotificationCmd {
}
