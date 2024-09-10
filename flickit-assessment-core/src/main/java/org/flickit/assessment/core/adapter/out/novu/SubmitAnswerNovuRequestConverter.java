package org.flickit.assessment.core.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import org.flickit.assessment.common.adapter.out.novu.NovuRequestConverter;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.COMPLETE_ASSESSMENT;

@Component
public class SubmitAnswerNovuRequestConverter implements NovuRequestConverter {

    @Override
    public TriggerEventRequest convert(NotificationEnvelope envelope) {
        var triggerEvent = new TriggerEventRequest();
        triggerEvent.setName(COMPLETE_ASSESSMENT.getCode());
        triggerEvent.setTo(createSubscriberRequest(envelope.targetUserId()));
        triggerEvent.setPayload(Map.of("data", envelope.payload(), "title", envelope.title()));
        return triggerEvent;
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
