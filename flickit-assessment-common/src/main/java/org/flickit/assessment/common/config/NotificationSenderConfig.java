package org.flickit.assessment.common.config;

import co.novu.common.base.Novu;
import co.novu.common.base.NovuConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(NotificationSenderProperties.class)
public class NotificationSenderConfig {

    public static final String NOTIFICATION_SENDER_THREAD_EXECUTOR = "notificationSenderThreadPoolTaskExecutor";

    @Bean(name = NOTIFICATION_SENDER_THREAD_EXECUTOR)
    Executor threadPoolTaskExecutor(NotificationSenderProperties properties) {
        var props = properties.getExecutor();
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setKeepAliveSeconds(props.getKeepAliveSeconds());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(props.isAllowCoreThreadTimeOut());
        executor.setPrestartAllCoreThreads(props.isPrestartAllCoreThreads());
        return executor;
    }

    @Bean
    Novu novu(NotificationSenderProperties properties) {
        NovuConfig novuConfig = new NovuConfig(properties.getNovu().getApiKey());
        novuConfig.setBaseUrl(properties.getNovu().getBaseUrl());
        return new Novu(novuConfig);
    }
}
