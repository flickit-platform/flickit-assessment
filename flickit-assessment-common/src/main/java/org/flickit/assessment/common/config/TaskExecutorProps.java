package org.flickit.assessment.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Properties to configure {@link ThreadPoolTaskExecutor} instances.
 *
 * @see ThreadPoolTaskExecutor
 */
@Getter
@Setter
@ToString
public class TaskExecutorProps {

    private int corePoolSize = 1;
    private int maxPoolSize = 10;
    private int keepAliveSeconds = 60;
    private int queueCapacity = 100;
    private boolean allowCoreThreadTimeOut = false;
    private boolean prestartAllCoreThreads = false;
}
