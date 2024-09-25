package org.flickit.assessment.common.adapter.out.novu;

import org.flickit.assessment.common.config.NotificationSenderProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.adapter.out.novu.NovuSettingProvider.NOVU_SUBSCRIBER_HASH_KEY;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NovuSettingProviderTest {

    @InjectMocks
    private NovuSettingProvider novuSettingProvider;

    @Mock
    private NotificationSenderProperties notificationSenderProperties;

    @Test
    void testGetSettings_ValidInput_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        NotificationSenderProperties.NovuProps novuProps = new NotificationSenderProperties.NovuProps();
        novuProps.setApiKey("1513513");
        when(notificationSenderProperties.getNovu()).thenReturn(novuProps);

        Map<String, String> settings = novuSettingProvider.getSettings(currentUserId);
        assertNotNull(settings.get(NOVU_SUBSCRIBER_HASH_KEY));
        assertNotEquals(currentUserId.toString(), settings.get(NOVU_SUBSCRIBER_HASH_KEY));
    }
}