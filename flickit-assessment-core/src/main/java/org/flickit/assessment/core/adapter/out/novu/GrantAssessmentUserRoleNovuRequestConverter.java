package org.flickit.assessment.core.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import org.flickit.assessment.common.adapter.out.novu.NovuRequestConverter;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.GRANT_USER_ASSESSMENT_ROLE;

@Component
public class GrantAssessmentUserRoleNovuRequestConverter implements NovuRequestConverter {

    @Override
    public TriggerEventRequest convert(NotificationEnvelope envelope) {
        var triggerEvent = new TriggerEventRequest();
        triggerEvent.setName(GRANT_USER_ASSESSMENT_ROLE.getCode());
        triggerEvent.setTo(createSubscriberRequest(envelope.targetUserId()));
        triggerEvent.setPayload(Map.of("data", envelope.payload(), "title", envelope.notificationTitle()));
        return triggerEvent;
    }

    private SubscriberRequest createSubscriberRequest(UUID targetUserId) {
        var subscriber = new SubscriberRequest();
        subscriber.setSubscriberId(targetUserId.toString());
        return subscriber;
    }

    @Override
    public Class<GrantAssessmentUserRoleNotificationPayload> payloadClass() {
        return GrantAssessmentUserRoleNotificationPayload.class;
    }
}
