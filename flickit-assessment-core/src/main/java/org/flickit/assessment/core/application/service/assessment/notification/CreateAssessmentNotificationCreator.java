package org.flickit.assessment.core.application.service.assessment.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope.User;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.notification.CreateAssessmentNotificationCmd;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitInfoPort;
import org.flickit.assessment.core.application.port.out.expertgroup.LoadExpertGroupMembersPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.KitModel;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.core.common.MessageKey.NOTIFICATION_TITLE_CREATE_ASSESSMENT;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateAssessmentNotificationCreator implements NotificationCreator<CreateAssessmentNotificationCmd> {

    private final LoadKitInfoPort loadKitInfoPort;
    private final LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(CreateAssessmentNotificationCmd cmd) {
        String displayName;
        var userOptional = loadUserPort.loadById(cmd.creatorId());
        if (userOptional.isEmpty()) {
            log.warn("user not found");
            return List.of();
        } else
            displayName = userOptional.get().getDisplayName();

        try {
            var title = MessageBundle.message(NOTIFICATION_TITLE_CREATE_ASSESSMENT);
            var kitInfo = loadKitInfoPort.loadKitInfo(cmd.kitId());
            var members = loadExpertGroupMembersPort.loadExpertGroupMembers(kitInfo.expertGroupId());

            KitModel kitModel = new KitModel(cmd.kitId(), kitInfo.title());
            UserModel userModel = new UserModel(displayName);
            var payload = new CreateAssessmentNotificationPayload(kitModel, userModel);
            return members.stream()
                .map(e -> new NotificationEnvelope(new User(e.id(), null), title, payload))
                .toList();
        } catch (ResourceNotFoundException e) {
            log.warn("kit not found");
            return List.of();
        }
    }

    @Override
    public Class<CreateAssessmentNotificationCmd> cmdClass() {
        return CreateAssessmentNotificationCmd.class;
    }
}
