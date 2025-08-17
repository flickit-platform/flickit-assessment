package org.flickit.assessment.core.application.service.assessmentuserrole.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRoleNotificationCmd;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload.AssessmentModel;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload.RoleModel;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.flickit.assessment.core.common.MessageKey.NOTIFICATION_TITLE_GRANT_ASSESSMENT_USER_ROLE;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrantAssessmentUserRoleNotificationCreator
    implements NotificationCreator<GrantAssessmentUserRoleNotificationCmd> {

    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(GrantAssessmentUserRoleNotificationCmd cmd) {
        Optional<Assessment> assessment = loadAssessmentPort.loadById(cmd.assessmentId());
        Optional<User> user = loadUserPort.loadById(cmd.assignerUserId());
        var targetUser = loadUserPort.loadById(cmd.targetUserId())
            .map(x -> new NotificationEnvelope.User(x.getId(), x.getEmail()));
        if (assessment.isEmpty() || user.isEmpty() || targetUser.isEmpty()) {
            log.warn("assessment or user not found");
            return List.of();
        }
        var title = MessageBundle.message(NOTIFICATION_TITLE_GRANT_ASSESSMENT_USER_ROLE);
        var payload = new GrantAssessmentUserRoleNotificationPayload(
            new AssessmentModel(assessment.get()),
            new UserModel(user.get().getId(), user.get().getDisplayName()),
            new RoleModel(cmd.role().getTitle()));
        return List.of(new NotificationEnvelope(targetUser.get(), title, payload));
    }

    @Override
    public Class<GrantAssessmentUserRoleNotificationCmd> cmdClass() {
        return GrantAssessmentUserRoleNotificationCmd.class;
    }
}
