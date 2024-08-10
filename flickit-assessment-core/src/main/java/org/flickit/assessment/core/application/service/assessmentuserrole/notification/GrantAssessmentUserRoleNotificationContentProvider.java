package org.flickit.assessment.core.application.service.assessmentuserrole.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.notification.NotificationContentProvider;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrantAssessmentUserRoleNotificationContentProvider
    implements NotificationContentProvider<GrantAssessmentUserRoleNotificationCmd, GrantAssessmentUserRoleNotificationContent> {

    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;

    @Override
    public Optional<GrantAssessmentUserRoleNotificationContent> create(GrantAssessmentUserRoleNotificationCmd cmd) {
        Optional<Assessment> assessment = getAssessmentPort.getAssessmentById(cmd.assessmentId());
        Optional<User> user = loadUserPort.loadById(cmd.assignerUserId());
        if (assessment.isEmpty() || user.isEmpty()) {
            log.warn("assessment or user not found");
            return Optional.empty();
        }
        return Optional.of(new GrantAssessmentUserRoleNotificationContent(
            new GrantAssessmentUserRoleNotificationContent.AssessmentModel(assessment.get().getId(), assessment.get().getTitle()),
            new GrantAssessmentUserRoleNotificationContent.UserModel(user.get().getId(), user.get().getDisplayName()),
            new GrantAssessmentUserRoleNotificationContent.RoleModel(cmd.role().getTitle())
        ));
    }

    @Override
    public Class<GrantAssessmentUserRoleNotificationCmd> cmdClass() {
        return GrantAssessmentUserRoleNotificationCmd.class;
    }
}
