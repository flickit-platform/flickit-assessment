package org.flickit.flickitassessmentcore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FlickitPlatformRestProperties.class)
public class FlickitPlatformRestClientConfig {

    @Bean
    RestTemplate flickitPlatformRestTemplate(FlickitPlatformRestProperties props) {
        return new RestTemplateBuilder()
            .setConnectTimeout(props.getRestClient().getConnectTimeout())
            .setReadTimeout(props.getRestClient().getReadTimeout())
            .messageConverters(new MappingJackson2HttpMessageConverter())
            .build();
    }
}
