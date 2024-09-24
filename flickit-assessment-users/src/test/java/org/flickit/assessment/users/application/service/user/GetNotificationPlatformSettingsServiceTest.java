package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.application.domain.notification.NotificationSenderSettingProvider;
import org.flickit.assessment.users.application.port.in.user.GetNotificationPlatformSettingsUseCase;
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
    void testGetNotificationPlatformSettingsTest_ValidInput_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID hash = UUID.randomUUID();
        when(settingProvider.getSettings(currentUserId)).thenReturn(Map.of("hash", hash.toString()));
        GetNotificationPlatformSettingsUseCase.Param param = new GetNotificationPlatformSettingsUseCase.Param(currentUserId);

        GetNotificationPlatformSettingsUseCase.Result result = service.getNotificationPlatformSettings(param);
        assertNotNull(result.settings());
        assertEquals(hash.toString(), result.settings().get("hash"));
    }
}
