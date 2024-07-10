package org.flickit.assessment.common.config;

import co.novu.common.base.Novu;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NovuClientProperties.class)
public class NovuClientConfig {

    @Bean
    Novu novu(NovuClientProperties properties) {
        return new Novu(properties.getApiKey());
    }
}
