package org.flickit.assessment.core.application.service.assessmentinvite.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.domain.notification.AcceptAssessmentInvitationNotificationsCmd;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessmentinvite.notification.AcceptAssessmentInvitationNotificationPayload.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.flickit.assessment.core.common.ErrorMessageKey.NOTIFICATION_TITLE_ACCEPT_ASSESSMENT_INVITATION;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcceptAssessmentInvitationNotificationCreator
    implements NotificationCreator<AcceptAssessmentInvitationNotificationsCmd> {

    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(AcceptAssessmentInvitationNotificationsCmd cmd) {
        return cmd.notificationCmdItems().stream()
            .map(notificationCmdItem -> {
                Optional<Assessment> assessment = getAssessmentPort.getAssessmentById(notificationCmdItem.assessmentId());
                Optional<User> user = loadUserPort.loadById(notificationCmdItem.inviteeId());
                var targetUser = loadUserPort.loadById(notificationCmdItem.targetUserId())
                    .map(x -> new NotificationEnvelope.User(x.getId(), x.getEmail()));

                if (assessment.isEmpty() || user.isEmpty() || targetUser.isEmpty()) {
                    log.warn("Assessment or user not found");
                    return null;
                }

                var title = MessageBundle.message(NOTIFICATION_TITLE_ACCEPT_ASSESSMENT_INVITATION);
                var payload = new AcceptAssessmentInvitationNotificationPayload(
                    new AssessmentModel(notificationCmdItem.assessmentId(), assessment.get().getTitle()),
                    new InviteeModel(notificationCmdItem.inviteeId(), user.get().getDisplayName()));

                return new NotificationEnvelope(targetUser.get(), title, payload);
            })
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public Class<AcceptAssessmentInvitationNotificationsCmd> cmdClass() {
        return AcceptAssessmentInvitationNotificationsCmd.class;
    }
}
