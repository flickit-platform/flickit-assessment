package org.flickit.assessment.core.application.service.assessmentinvite.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessmentinvite.notification.AcceptAssessmentInvitationNotificationPayload.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcceptAssessmentInvitationNotificationCreator
    implements NotificationCreator<AcceptAssessmentInvitationNotificationCmd> {

    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(AcceptAssessmentInvitationNotificationCmd cmd) {
        Optional<Assessment> assessment = getAssessmentPort.getAssessmentById(cmd.assessmentId());
        Optional<User> user = loadUserPort.loadById(cmd.inviteeUserId());
        if (assessment.isEmpty() || user.isEmpty()) {
            log.warn("assessment or user not found");
            return List.of();
        }
        return List.of(
            new NotificationEnvelope(cmd.targetUserId(), new AcceptAssessmentInvitationNotificationPayload(
                new AssessmentModel(assessment.get().getId(), assessment.get().getTitle()),
                new InviteeModel(user.get().getId(), user.get().getDisplayName()),
                new RoleModel(cmd.assessmentUserRole().getTitle())
            )));
    }

    @Override
    public Class cmdClass() {
        return AcceptAssessmentInvitationNotificationCmd.class;
    }
}
