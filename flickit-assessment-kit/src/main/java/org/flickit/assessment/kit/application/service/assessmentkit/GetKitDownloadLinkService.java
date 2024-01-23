package org.flickit.assessment.kit.application.service.assessmentkit;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadKitDownloadLinkPort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDownloadLinkService implements GetKitDownloadLinkUseCase {

    private final LoadKitDownloadLinkPort loadKitDownloadLinkPort;
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @SneakyThrows
    @Override
    public String getKitLink(Param param) {
        String objectName = loadKitDownloadLinkPort.loadKitDownloadLink(param.getKitId());
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(properties.getBucketName())
            .object(objectName)
            .expiry(5, TimeUnit.MINUTES)
            .method(Method.GET)
            .build());
    }
}
