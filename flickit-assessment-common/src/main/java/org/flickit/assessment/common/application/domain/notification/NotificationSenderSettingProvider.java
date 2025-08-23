package org.flickit.assessment.common.application.domain.notification;

import java.util.Map;
import java.util.UUID;

public interface NotificationSenderSettingProvider {

    Map<String, String> getSettings(UUID userId);
}
