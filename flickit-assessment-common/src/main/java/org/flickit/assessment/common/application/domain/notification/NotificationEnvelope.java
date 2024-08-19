package org.flickit.assessment.common.application.domain.notification;

import java.util.UUID;

public record NotificationEnvelope(UUID targetUserId,
                                   NotificationPayload payload) {
}
