package org.flickit.assessment.common.application.domain.notification;

import java.util.List;

public interface NotificationCreator<I extends NotificationCmd> {

    List<NotificationEnvelope> create(I cmd);

    Class<I> cmdClass();
}
