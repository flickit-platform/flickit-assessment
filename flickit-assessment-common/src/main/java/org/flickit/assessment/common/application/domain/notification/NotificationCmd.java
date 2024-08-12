package org.flickit.assessment.common.application.domain.notification;

import java.util.UUID;

public interface NotificationCmd {

    /**
     * The userId which is the target of notification
     */
    UUID targetUserId();
}
