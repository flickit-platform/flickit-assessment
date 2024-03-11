package org.flickit.assessment.users.config;

import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("usersMinioConfig")
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioConfig {

    @Bean("usersMinioClient")
    @SneakyThrows
    public MinioClient minioClient(MinioConfigProperties properties) {
        return MinioClient.builder()
            .credentials(properties.getAccessKey(), properties.getAccessSecret())
            .endpoint(properties.getUrl(), properties.getPort(), properties.getSecure())
            .build();
    }
}
