package org.flickit.assessment.kit.config;

import io.minio.*;
import io.minio.messages.VersioningConfiguration;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.flickit.assessment.kit")
@EnableConfigurationProperties({MinioConfigProperties.class, DslParserRestProperties.class})
public class AssessmentKitAutoConfig {

    @Bean
    @SneakyThrows
    public MinioClient minioClient(MinioConfigProperties properties) {
        MinioClient minioClient = MinioClient.builder()
            .credentials(properties.getAccessKey(), properties.getAccessSecret())
            .endpoint(properties.getUrl(), properties.getPort(), properties.getSecure())
            .build();

        createMinioBucket(minioClient, properties.getBucketNames().getDsl());
        createMinioBucket(minioClient, properties.getBucketNames().getAvatar());

        return minioClient;
    }

    @SneakyThrows
    private void createMinioBucket(MinioClient minioClient, String bucketName) {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .build());
        }

        minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
            .bucket(bucketName)
            .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false))
            .build());
    }
}
