package org.flickit.assessment.common.application.domain.notification;

import java.util.Optional;

public interface NotificationContentProvider<I extends NotificationCmd, O extends NotificationContent> {

    Optional<O> create(I cmd);

    Class<I> cmdClass();
}
