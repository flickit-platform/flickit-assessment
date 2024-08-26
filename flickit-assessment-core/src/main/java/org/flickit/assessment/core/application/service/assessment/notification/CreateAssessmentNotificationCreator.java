package org.flickit.assessment.core.application.service.assessment.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitInfoPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.AssessmentModel;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.KitModel;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.UserModel;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload.SpaceModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateAssessmentNotificationCreator implements NotificationCreator<CreateAssessmentNotificationCmd> {

    private final LoadKitInfoPort loadKitInfoPort;
    private final LoadSpacePort loadSpacePort;
    private final LoadUserPort loadUserPort;


    @Override
    public List<NotificationEnvelope> create(CreateAssessmentNotificationCmd cmd) {
        var user = loadUserPort.loadById(cmd.assessmentCreatedBy());
        if (user.isEmpty()) {
            log.warn("user not found");
            return List.of();
        }

        try {
            var kitInfo = loadKitInfoPort.loadKitInfo(cmd.kitId());
            var space = loadSpacePort.load(cmd.spaceId());
            AssessmentModel assessmentModel = new AssessmentModel(cmd.assessmentId(), cmd.assessmentTitle());
            KitModel kitModel = new KitModel(cmd.kitId(), kitInfo.title());
            SpaceModel spaceModel = new SpaceModel(space.getId(), space.getTitle());
            UserModel userModel = new UserModel(user.get().getId(), user.get().getDisplayName());
            CreateAssessmentNotificationPayload payload = new CreateAssessmentNotificationPayload(assessmentModel,
                userModel,
                kitModel,
                spaceModel);
            return List.of(new NotificationEnvelope(kitInfo.createdBy(), payload));
        } catch (ResourceNotFoundException e) {
            log.warn("space or kit not found");
            return List.of();
        }
    }

    @Override
    public Class<CreateAssessmentNotificationCmd> cmdClass() {
        return CreateAssessmentNotificationCmd.class;
    }
}
