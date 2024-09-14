package org.flickit.assessment.core.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import org.flickit.assessment.common.adapter.out.novu.NovuRequestConverter;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.COMPLETE_ASSESSMENT;

@Component
public class SubmitAnswerNovuRequestConverter implements NovuRequestConverter {

    @Override
    public TriggerEventRequest convert(NotificationEnvelope envelope) {
        var triggerEvent = new TriggerEventRequest();
        triggerEvent.setName(COMPLETE_ASSESSMENT.getCode());
        triggerEvent.setTo(createSubscriberRequest(envelope.targetUser()));
        triggerEvent.setPayload(Map.of("data", envelope.payload(), "title", envelope.title()));
        return triggerEvent;
    }

    private SubscriberRequest createSubscriberRequest(NotificationEnvelope.User targetUser) {
        var subscriber = new SubscriberRequest();
        subscriber.setSubscriberId(targetUser.id().toString());
        subscriber.setEmail(targetUser.email());
        return subscriber;
    }

    @Override
    public Class<SubmitAnswerNotificationPayload> payloadClass() {
        return SubmitAnswerNotificationPayload.class;
    }
}
