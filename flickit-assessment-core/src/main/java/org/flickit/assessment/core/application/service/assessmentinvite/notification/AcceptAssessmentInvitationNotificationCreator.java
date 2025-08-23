package org.flickit.assessment.core.application.service.assessmentinvite.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.domain.notification.AcceptAssessmentInvitationNotificationsCmd;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessmentinvite.notification.AcceptAssessmentInvitationNotificationPayload.InviteeModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.flickit.assessment.core.common.MessageKey.NOTIFICATION_TITLE_ACCEPT_ASSESSMENT_INVITATION;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcceptAssessmentInvitationNotificationCreator
    implements NotificationCreator<AcceptAssessmentInvitationNotificationsCmd> {

    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(AcceptAssessmentInvitationNotificationsCmd cmd) {
        return cmd.targetUserIds().stream()
            .map(targetUserId -> {
                Optional<User> user = loadUserPort.loadById(cmd.inviteeId());
                var targetUser = loadUserPort.loadById(targetUserId)
                    .map(x -> new NotificationEnvelope.User(x.getId(), x.getEmail()));

                if (user.isEmpty() || targetUser.isEmpty()) {
                    log.warn("User not found");
                    return null;
                }

                var title = MessageBundle.message(NOTIFICATION_TITLE_ACCEPT_ASSESSMENT_INVITATION);
                var payload = new AcceptAssessmentInvitationNotificationPayload(
                    new InviteeModel(cmd.inviteeId(), user.get().getDisplayName()));

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
