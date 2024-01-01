package org.flickit.assessment.kit.adapter.out.uploaddsl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitPort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@AllArgsConstructor
public class MinioAdapter implements UploadKitPort {

    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @Override
    public void upload(MultipartFile dslFile) {
        String bucketName = properties.getBucketName();
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }

        try {
            InputStream inputStream = dslFile.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(properties.getObjectName() + dslFile.getName())
                .stream(inputStream, inputStream.available(), -1)
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
