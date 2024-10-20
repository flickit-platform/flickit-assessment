package org.flickit.assessment.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@EnableRetry
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(EmailProperties.class)
public class EmailConfig {

    private final EmailProperties properties;

    public static final String EMAIL_SENDER_THREAD_EXECUTOR = "emailSenderThreadPoolTaskExecutor";

    @Bean(name = EMAIL_SENDER_THREAD_EXECUTOR)
    Executor threadPoolTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getExecutor().getCorePoolSize());
        executor.setMaxPoolSize(properties.getExecutor().getMaxPoolSize());
        executor.setKeepAliveSeconds(properties.getExecutor().getKeepAliveSeconds());
        executor.setQueueCapacity(properties.getExecutor().getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(properties.getExecutor().isAllowCoreThreadTimeOut());
        executor.setPrestartAllCoreThreads(properties.getExecutor().isPrestartAllCoreThreads());
        return executor;
    }
}
