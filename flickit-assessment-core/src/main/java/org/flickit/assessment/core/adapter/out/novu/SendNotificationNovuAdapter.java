package org.flickit.assessment.core.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import co.novu.common.base.Novu;
import co.novu.common.rest.NovuNetworkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.NotificationType;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.notification.SendNotificationPort;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendNotificationNovuAdapter implements SendNotificationPort {

    private final Novu novu;
    private final UserJpaRepository userRepository;

    @Override
    public void sendNotification(UUID userId, NotificationType type, Object data) {
        TriggerEventRequest triggerEventRequest = new TriggerEventRequest();
        triggerEventRequest.setName(type.getCode());
        triggerEventRequest.setTo(createSubscriberRequest(userId));
        triggerEventRequest.setPayload(Map.of("data", data));
        try {
            novu.triggerEvent(triggerEventRequest);
        } catch (IOException e) {
            log.error("Failed to send notification due to an I/O error. UserId: {}, NotificationType: {}", userId, type, e);
        } catch (NovuNetworkException e) {
            log.error("Failed to send notification due to a network error. UserId: {}, NotificationType: {}", userId, type, e);
        }
    }

    private SubscriberRequest createSubscriberRequest(UUID userId) {
        UserJpaEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_USER_NOT_FOUND));

        SubscriberRequest subscriberRequest = new SubscriberRequest();
        subscriberRequest.setSubscriberId(userId.toString());
        subscriberRequest.setData(Map.of(UserJpaEntity.Fields.NAME, user.getDisplayName()));
        return subscriberRequest;
    }
}
