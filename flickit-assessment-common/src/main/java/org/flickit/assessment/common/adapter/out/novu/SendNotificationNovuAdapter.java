package org.flickit.assessment.common.adapter.out.novu;

import co.novu.api.events.requests.TriggerEventRequest;
import co.novu.common.base.Novu;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.Tenant;
import org.flickit.assessment.common.application.port.out.SendNotificationPort;
import org.flickit.assessment.common.config.NotificationSenderProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class SendNotificationNovuAdapter implements SendNotificationPort {

    private final Novu novu;
    private final Tenant tenant;
    private final Map<Class<?>, NovuRequestConverter> converters;

    public SendNotificationNovuAdapter(Novu novu, NotificationSenderProperties properties, List<NovuRequestConverter> converters) {
        this.novu = novu;
        this.tenant = new Tenant(properties.getNovu().getTenantId());
        this.converters = converters.stream().collect(toMap(NovuRequestConverter::payloadClass, x -> x));
    }

    @Override
    public void send(NotificationEnvelope envelope) {
        var converter = converters.get(envelope.payload().getClass());
        TriggerEventRequest triggerEventRequest = converter.convert(envelope);
        triggerEventRequest.setTenant(tenant);
        try {
            novu.triggerEvent(triggerEventRequest);
        } catch (Exception e) {
            log.error("Failed to send notification[targetUserId={}, payload=[{}].",
                envelope.targetUser().id(), envelope.payload().getClass().getSimpleName(), e);
        }
    }
}
