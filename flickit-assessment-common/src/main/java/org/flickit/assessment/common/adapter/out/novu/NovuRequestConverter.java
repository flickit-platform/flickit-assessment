package org.flickit.assessment.common.adapter.out.novu;

import co.novu.api.events.requests.TriggerEventRequest;
import org.flickit.assessment.common.application.domain.notification.NotificationContent;

import java.util.UUID;

public interface NovuRequestConverter<I extends NotificationContent> {

    TriggerEventRequest convert(UUID targetUserId, I content);

    Class<I> contentClass();
}
