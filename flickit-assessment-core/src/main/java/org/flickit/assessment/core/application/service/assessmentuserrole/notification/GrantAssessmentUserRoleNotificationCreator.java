package org.flickit.assessment.core.application.service.assessmentuserrole.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRoleNotificationCmd;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload.AssessmentModel;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload.RoleModel;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_NOTIFICATION_TITLE;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrantAssessmentUserRoleNotificationCreator
    implements NotificationCreator<GrantAssessmentUserRoleNotificationCmd> {

    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(GrantAssessmentUserRoleNotificationCmd cmd) {
        Optional<Assessment> assessment = getAssessmentPort.getAssessmentById(cmd.assessmentId());
        Optional<User> user = loadUserPort.loadById(cmd.assignerUserId());
        if (assessment.isEmpty() || user.isEmpty()) {
            log.warn("assessment or user not found");
            return List.of();
        }
        return List.of(
            new NotificationEnvelope(cmd.targetUserId(),
                new GrantAssessmentUserRoleNotificationPayload(
                    new AssessmentModel(assessment.get().getId(), assessment.get().getTitle()),
                    new UserModel(user.get().getId(), user.get().getDisplayName()),
                    new RoleModel(cmd.role().getTitle())),
                MessageBundle.message(GRANT_ASSESSMENT_USER_ROLE_NOTIFICATION_TITLE))
        );
    }

    @Override
    public Class<GrantAssessmentUserRoleNotificationCmd> cmdClass() {
        return GrantAssessmentUserRoleNotificationCmd.class;
    }
}
