package org.flickit.assessment.users.application.service.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationSenderSettingProvider;
import org.flickit.assessment.users.application.port.in.notification.GetNotificationPlatformSettingsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNotificationPlatformSettingsServiceTest {

    @InjectMocks
    private GetNotificationPlatformSettingsService service;

    @Mock
    private NotificationSenderSettingProvider settingProvider;

    @Test
    void testGetNotificationPlatformSettings_ValidInput_ValidResult() {
        UUID hash = UUID.randomUUID();
        var param = new GetNotificationPlatformSettingsUseCase.Param(UUID.randomUUID());

        var settingsMap = Map.of("hash", hash.toString());

        when(settingProvider.getSettings(param.getCurrentUserId())).thenReturn(settingsMap);

        var result = service.getNotificationPlatformSettings(param);

        assertNotNull(result.settings());
        assertEquals(settingsMap.size(), result.settings().size());
        assertNotNull(result.settings().get("hash"));
        assertEquals(hash.toString(), result.settings().get("hash"));
    }
}
