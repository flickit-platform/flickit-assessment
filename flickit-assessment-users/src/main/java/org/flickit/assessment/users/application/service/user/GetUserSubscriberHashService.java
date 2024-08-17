package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.flickit.assessment.common.config.NotificationSenderProperties;
import org.flickit.assessment.users.application.port.in.user.GetUserSubscriberHashUseCase;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class GetUserSubscriberHashService implements GetUserSubscriberHashUseCase {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final NotificationSenderProperties notificationSenderProperties;

    @Override
    public Result getUserSubscriberHash(Param param) {
        var key = notificationSenderProperties.getNovu().getApiKey();
        var subscriberHash = encode(key, param.getCurrentUserId().toString());
        return new Result(subscriberHash);
    }

    @SneakyThrows
    private String encode(String key, String subscriberId) {
        var hmacEncryptor = Mac.getInstance(HMAC_SHA256);
        var secretKeySpec = new SecretKeySpec(key.getBytes(UTF_8), HMAC_SHA256);
        hmacEncryptor.init(secretKeySpec);
        hmacEncryptor.update(subscriberId.getBytes());
        return Hex.encodeHexString(hmacEncryptor.doFinal());
    }
}
