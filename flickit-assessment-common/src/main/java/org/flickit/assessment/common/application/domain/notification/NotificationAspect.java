package org.flickit.assessment.common.application.domain.notification;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.config.NotificationSenderConfig.NOTIFICATION_SENDER_THREAD_EXECUTOR;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "app.notif-sender.enabled", havingValue = "true")
public class NotificationAspect {

    private final SendNotificationPort sendNotificationPort;
    private final Map<Class<?>, NotificationContentProvider> contentProviders;

    public NotificationAspect(SendNotificationPort sendNotificationPort,
                              List<NotificationContentProvider<?, ?>> contentProviders) {
        this.sendNotificationPort = sendNotificationPort;
        this.contentProviders = contentProviders.stream()
            .collect(toMap(NotificationContentProvider::cmdClass, x -> x));
    }

    @Pointcut("@annotation(SendNotification)")
    public void annotatedBySendNotification() {
    }

    @Async(NOTIFICATION_SENDER_THREAD_EXECUTOR)
    @AfterReturning(pointcut = "annotatedBySendNotification()", returning = "result")
    public void sendNotificationAfter(Object result) {
        if (!(result instanceof HasNotificationCmd hasCmd))
            return;

        NotificationCmd cmd = hasCmd.notificationCmd();
        log.debug("{} received.", cmd);
        var provider = contentProviders.get(cmd.getClass());
        Optional<NotificationContent> content = provider.create(cmd);
        content.ifPresent(x -> {
            log.debug("Send {}", x);
            sendNotificationPort.send(cmd.targetUserId(), x);
        });
    }
}
