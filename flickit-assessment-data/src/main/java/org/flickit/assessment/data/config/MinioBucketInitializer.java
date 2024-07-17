package org.flickit.assessment.data.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketVersioningArgs;
import io.minio.messages.VersioningConfiguration;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = "app.minio.init-buckets",
    havingValue = "true",
    matchIfMissing = true)
public class MinioBucketInitializer {

    public MinioBucketInitializer(MinioClient minioClient, MinioConfigProperties properties) {
        createMinioBucket(minioClient, properties.getBucketNames().getDsl());
        createMinioBucket(minioClient, properties.getBucketNames().getAvatar());
        createMinioBucket(minioClient, properties.getBucketNames().getAttachment());
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
