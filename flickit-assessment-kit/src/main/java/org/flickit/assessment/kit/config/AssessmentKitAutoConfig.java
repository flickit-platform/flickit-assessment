package org.flickit.assessment.kit.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.flickit.assessment.kit")
@EnableConfigurationProperties(MinioConfigProperties.class)
public class AssessmentKitAutoConfig {

    @Bean
    public MinioClient minioClient(MinioConfigProperties properties) {
        return MinioClient.builder()
            .credentials(properties.getAccessKey(), properties.getAccessSecret())
            .endpoint(properties.getUrl(), properties.getPort(), properties.getSecure())
            .build();
    }
}
