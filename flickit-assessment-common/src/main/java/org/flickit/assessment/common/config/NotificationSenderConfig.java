package org.flickit.assessment.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAspectJAutoProxy
public class NotificationSenderConfig {

    public static final String NOTIFICATION_SENDER_THREAD_EXECUTOR = "notificationSenderThreadPoolTaskExecutor";

    @Bean(name = NOTIFICATION_SENDER_THREAD_EXECUTOR)
    Executor notificationSenderThreadPoolTaskExecutor() {
        var props = new TaskExecutorProps();
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setKeepAliveSeconds(props.getKeepAliveSeconds());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(props.isAllowCoreThreadTimeOut());
        executor.setPrestartAllCoreThreads(props.isPrestartAllCoreThreads());
        return executor;
    }
}
