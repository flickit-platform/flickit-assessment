package org.flickit.assessment.kit.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.kit.application.service.assessmentkit.notification.ToggleKitLikeNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.TOGGLE_KIT_LIKE;

@Component
public class ToggleKitLikeNovuRequestConverter extends AbstractNovuRequestConverter {

    @Override
    protected String getEventName() {
        return TOGGLE_KIT_LIKE.getCode();
    }

    @Override
    public Class<ToggleKitLikeNotificationPayload> payloadClass() {
        return ToggleKitLikeNotificationPayload.class;
    }
}
