package org.flickit.assessment.common.adapter.out.novu;


import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.flickit.assessment.common.application.domain.notification.NotificationSenderSettingProvider;
import org.flickit.assessment.common.config.NotificationSenderProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class NovuSettingProvider implements NotificationSenderSettingProvider {

    public static final String NOVU_SUBSCRIBER_HASH_KEY = "novuSubscriberHash";

    private final NotificationSenderProperties notificationSenderProperties;

    @Override
    public Map<String, String> getSettings(UUID subscriberId) {
        return Map.of(NOVU_SUBSCRIBER_HASH_KEY, getSubscriberHash(subscriberId));
    }

    private String getSubscriberHash(UUID subscriberId) {
        var key = notificationSenderProperties.getNovu().getApiKey();
        var hmacHash = Hashing.hmacSha256(key.getBytes(UTF_8)).hashString(subscriberId.toString(), UTF_8);
        return Hex.encodeHexString(hmacHash.asBytes());
    }
}
