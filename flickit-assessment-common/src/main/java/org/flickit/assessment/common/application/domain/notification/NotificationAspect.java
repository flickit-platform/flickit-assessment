package org.flickit.assessment.common.application.domain.notification;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.flickit.assessment.common.application.port.out.SendNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.config.NotificationSenderConfig.NOTIFICATION_SENDER_THREAD_EXECUTOR;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "app.notif-sender.enabled", havingValue = "true")
public class NotificationAspect {

    private final SendNotificationPort sender;
    private final Map<Class<?>, NotificationCreator> creators;

    public NotificationAspect(SendNotificationPort sender,
                              List<NotificationCreator<?>> creators) {
        this.sender = sender;
        this.creators = creators.stream()
            .collect(toMap(NotificationCreator::cmdClass, x -> x));
    }

    @Async(NOTIFICATION_SENDER_THREAD_EXECUTOR)
    @AfterReturning(value = "@annotation(SendNotification)", returning = "result")
    public void sendNotificationAfter(Object result) {
        if (!(result instanceof HasNotificationCmd hasCmd))
            return;

        NotificationCmd cmd = hasCmd.notificationCmd();
        log.debug("{} received.", cmd);

        var creator = creators.get(cmd.getClass());
        List<NotificationEnvelope> envelopes = creator.create(cmd);

        envelopes.forEach(x -> {
            log.debug("Send {}", x);
            sender.send(x);
        });
    }
}
