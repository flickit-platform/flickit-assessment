package org.flickit.assessment.common.adapter.out.novu;

import co.novu.api.events.requests.TriggerEventRequest;
import co.novu.common.base.Novu;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.notification.NotificationContent;
import org.flickit.assessment.common.application.domain.notification.SendNotificationPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class SendNotificationNovuAdapter implements SendNotificationPort {

    private final Novu novu;
    private final Map<Class<?>, NovuRequestConverter> converters;

    public SendNotificationNovuAdapter(Novu novu, List<NovuRequestConverter<?>> converters) {
        this.novu = novu;
        this.converters = converters.stream().collect(toMap(NovuRequestConverter::contentClass, x -> x));
    }

    @Override
    public void send(UUID targetUserId, NotificationContent content) {
        var converter = converters.get(content.getClass());
        TriggerEventRequest triggerEventRequest = converter.convert(targetUserId, content);
        try {
            novu.triggerEvent(triggerEventRequest);
        } catch (Exception e) {
            log.error("Failed to send notification[targetUserId={}, notificationType=[{}].",
                targetUserId, content.getClass().getSimpleName(), e);
        }
    }
}
