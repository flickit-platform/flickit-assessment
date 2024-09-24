package org.flickit.assessment.users.application.service.notification;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.notification.NotificationSenderSettingProvider;
import org.flickit.assessment.users.application.port.in.notification.GetNotificationPlatformSettingsUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetNotificationPlatformSettingsService implements GetNotificationPlatformSettingsUseCase {

    private final NotificationSenderSettingProvider settingProvider;

    @Override
    public Result getNotificationPlatformSettings(Param param) {
        return new Result(settingProvider.getSettings(param.getCurrentUserId()));
    }
}
