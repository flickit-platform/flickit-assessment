package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.config.NotificationSenderProperties;
import org.flickit.assessment.users.application.port.in.user.GetUserSubscriberHashUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserSubscriberHashServiceTest {

    @InjectMocks
    private GetUserSubscriberHashService service;

    @Mock
    private NotificationSenderProperties notificationSenderProperties;

    @Test
    void testUpdateUserProfile_ValidInput_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        NotificationSenderProperties.NovuProps novuProps = new NotificationSenderProperties.NovuProps();
        novuProps.setApiKey("1513513");
        when(notificationSenderProperties.getNovu()).thenReturn(novuProps);
        GetUserSubscriberHashUseCase.Param param = new GetUserSubscriberHashUseCase.Param(currentUserId);

        GetUserSubscriberHashUseCase.Result result = service.getUserSubscriberHash(param);
        assertNotNull(result.subscriberHash());
        assertNotEquals(currentUserId.toString(), result.subscriberHash());
    }
}
