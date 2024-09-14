package org.flickit.assessment.core.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.adapter.out.novu.NovuRequestConverter;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.Tenant;
import org.flickit.assessment.common.config.NotificationSenderProperties;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.COMPLETE_ASSESSMENT;

@Component
@RequiredArgsConstructor
public class SubmitAnswerNovuRequestConverter implements NovuRequestConverter {

    private final NotificationSenderProperties properties;

    @Override
    public TriggerEventRequest convert(NotificationEnvelope envelope) {
        var triggerEvent = new TriggerEventRequest();
        triggerEvent.setTenant(createTenant());
        triggerEvent.setName(COMPLETE_ASSESSMENT.getCode());
        triggerEvent.setTo(createSubscriberRequest(envelope.targetUserId()));
        triggerEvent.setPayload(Map.of("data", envelope.payload(), "title", envelope.title()));
        return triggerEvent;
    }

    private Tenant createTenant() {
        return new Tenant(properties.getNovu().getTenantId());
    }

    private SubscriberRequest createSubscriberRequest(UUID targetUserId) {
        var subscriber = new SubscriberRequest();
        subscriber.setSubscriberId(targetUserId.toString());
        return subscriber;
    }

    @Override
    public Class<SubmitAnswerNotificationPayload> payloadClass() {
        return SubmitAnswerNotificationPayload.class;
    }
}
