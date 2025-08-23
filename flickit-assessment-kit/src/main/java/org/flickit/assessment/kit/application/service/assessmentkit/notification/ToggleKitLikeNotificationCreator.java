package org.flickit.assessment.kit.application.service.assessmentkit.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserPort;
import org.flickit.assessment.kit.application.service.assessmentkit.notification.ToggleKitLikeNotificationPayload.AssessmentKitModel;
import org.flickit.assessment.kit.application.service.assessmentkit.notification.ToggleKitLikeNotificationPayload.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.kit.common.MessageKey.NOTIFICATION_TITLE_TOGGLE_KIT_LIKE;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ToggleKitLikeNotificationCreator
    implements NotificationCreator<ToggleKitLikeNotificationCmd> {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(ToggleKitLikeNotificationCmd cmd) {
        if (!cmd.liked()) {
            log.debug("kit unliked");
            return List.of();
        }
        AssessmentKit assessmentKit = loadAssessmentKitPort.load(cmd.kitId());

        var createdBy = loadUserPort.loadById(cmd.targetUserId())
            .map(x -> new NotificationEnvelope.User(x.getId(), x.getEmail()));
        if (createdBy.isEmpty()) {
            log.warn("user not found");
            return List.of();
        }

        var title = MessageBundle.message(NOTIFICATION_TITLE_TOGGLE_KIT_LIKE);
        var payload = new ToggleKitLikeNotificationPayload(
            new AssessmentKitModel(assessmentKit.getId(), assessmentKit.getTitle()),
            new UserModel(createdBy.get().id(), createdBy.get().email())
        );
        return List.of(
            new NotificationEnvelope(createdBy.get(), title, payload)
        );
    }

    @Override
    public Class<ToggleKitLikeNotificationCmd> cmdClass() {
        return ToggleKitLikeNotificationCmd.class;
    }
}
