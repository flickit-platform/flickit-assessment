package org.flickit.assessment.common.application.domain.notification;

import java.util.UUID;

public record NotificationEnvelope(User targetUser,
                                   String title,
                                   NotificationPayload payload) {

    public record User(UUID id,
                       String email) {
    }
}
