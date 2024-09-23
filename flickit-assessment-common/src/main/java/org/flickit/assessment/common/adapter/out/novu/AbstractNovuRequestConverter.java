package org.flickit.assessment.common.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope.User;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractNovuRequestConverter implements NovuRequestConverter {

    @Override
    public TriggerEventRequest convert(NotificationEnvelope envelope) {
        var triggerEvent = new TriggerEventRequest();
        triggerEvent.setName(getEventName());
        triggerEvent.setTo(createSubscriberRequest(envelope.targetUser()));
        triggerEvent.setPayload(Map.of("data", envelope.payload(), "title", envelope.title()));
        return triggerEvent;
    }

    protected abstract String getEventName();

    private SubscriberRequest createSubscriberRequest(User targetUser) {
        var subscriber = new SubscriberRequest();
        subscriber.setSubscriberId(targetUser.id().toString());
        subscriber.setEmail(targetUser.email());
        return subscriber;
    }
}
