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

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getDslBucketName()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(properties.getDslBucketName())
                .build());
        }

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getAvatarBucketName()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(properties.getAvatarBucketName())
                .build());
        }

        minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
            .bucket(properties.getDslBucketName())
            .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false))
            .build());

        minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
            .bucket(properties.getAvatarBucketName())
            .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false))
            .build());

        return minioClient;
    }
}
