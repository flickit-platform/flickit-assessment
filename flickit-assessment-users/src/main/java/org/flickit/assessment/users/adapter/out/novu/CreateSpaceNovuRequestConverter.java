package org.flickit.assessment.users.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.users.application.service.space.notification.CreateSpaceNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.CREATE_SPACE;

@Component
public class CreateSpaceNovuRequestConverter extends AbstractNovuRequestConverter {

    @Override
    protected String getEventName() {
        return CREATE_SPACE.getCode();
    }

    @Override
    public Class<CreateSpaceNotificationPayload> payloadClass() {
        return CreateSpaceNotificationPayload.class;
    }
}
