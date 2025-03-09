package org.flickit.assessment.users.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.users.application.service.space.notification.CreatePremiumSpaceNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.CREATE_PREMIUM_SPACE;

@Component
public class CreatePremiumSpaceNovuRequestConverter extends AbstractNovuRequestConverter {

    @Override
    protected String getEventName() {
        return CREATE_PREMIUM_SPACE.getCode();
    }

    @Override
    public Class<CreatePremiumSpaceNotificationPayload> payloadClass() {
        return CreatePremiumSpaceNotificationPayload.class;
    }
}
