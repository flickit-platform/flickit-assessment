package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KitDslMinioAdapter implements CreateFileDownloadLinkPort {

    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @SneakyThrows
    @Override
    public String createDownloadLink(String filePath, Duration expiryDuration) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(properties.getBucketName())
            .object(filePath)
            .expiry((int) expiryDuration.getSeconds(), TimeUnit.SECONDS)
            .method(Method.GET)
            .build());
    }
}
