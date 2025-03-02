package org.flickit.assessment.users.application.service.space.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope.User;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.domain.notification.CreatePremiumSpaceNotificationCmd;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.users.common.MessageKey.NOTIFICATION_TITLE_CREATE_PREMIUM_SPACE;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateSpaceNotificationCreator implements NotificationCreator<CreatePremiumSpaceNotificationCmd> {

    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(CreatePremiumSpaceNotificationCmd cmd) {
        if(SpaceType.BASIC.getCode().equals(cmd.space().getType().getCode()))
            return List.of();

        var user = loadUserPort.loadUser(cmd.space().getCreatedBy());
        var adminId = loadUserPort.loadUserIdByEmail(cmd.adminEmail());

        if (user == null || adminId.isEmpty()) {
            log.warn("user not found");
            return List.of();
        }

        var userModel = new CreatePremiumSpaceNotificationPayload.UserModel(user.getDisplayName(), user.getEmail());
        var spaceModel = new CreatePremiumSpaceNotificationPayload.SpaceModel(cmd.space().getTitle(), cmd.space().getCreationTime());
        var title = MessageBundle.message(NOTIFICATION_TITLE_CREATE_PREMIUM_SPACE);
        var payload = new CreatePremiumSpaceNotificationPayload(userModel, spaceModel);

        return List.of(new NotificationEnvelope(new User(adminId.get(), cmd.adminEmail()), title, payload));
    }

    @Override
    public Class<CreatePremiumSpaceNotificationCmd> cmdClass() {
        return CreatePremiumSpaceNotificationCmd.class;
    }
}
