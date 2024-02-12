package org.flickit.assessment.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FlickitPlatformRestProperties.class)
public class FlickitPlatformRestClientConfig {

    @Bean
    RestTemplate flickitPlatformRestTemplate(FlickitPlatformRestProperties props, RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setConnectTimeout(props.getRestClient().getConnectTimeout())
            .setReadTimeout(props.getRestClient().getReadTimeout())
            .messageConverters(mappingJackson2HttpMessageConverter())
            .build();
    }

    private static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false));
        return converter;
    }
}
