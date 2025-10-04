package org.flickit.assessment.data.config;

import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.minio.http.HttpUtils.newDefaultHttpClient;

@Configuration
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioConfig {

    @Bean
    @SneakyThrows
    public MinioClient minioClient(MinioConfigProperties properties) {
        var httpProps = properties.getHttpClient();
        var httpClient = newDefaultHttpClient(
            httpProps.getConnectTimeout().toMillis(),
            httpProps.getWriteTimeout().toMillis(),
            httpProps.getReadTimeout().toMillis()
        );
        return MinioClient.builder()
            .credentials(properties.getAccessKey(), properties.getAccessSecret())
            .endpoint(properties.getUrl(), properties.getPort(), properties.getSecure())
            .httpClient(httpClient)
            .build();
    }
}
