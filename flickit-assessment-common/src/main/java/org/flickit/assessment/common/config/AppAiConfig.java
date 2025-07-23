package org.flickit.assessment.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AppAiProperties.class)
public class AppAiConfig {

    private final AppAiProperties properties;

    @Bean
    Executor attributeInsightExecutor() {
        var props = properties.getExecutors().getAttributeInsight();
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
