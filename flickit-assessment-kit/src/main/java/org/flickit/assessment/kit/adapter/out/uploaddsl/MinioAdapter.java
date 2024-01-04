package org.flickit.assessment.kit.adapter.out.uploaddsl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.VersioningConfiguration;
import io.minio.messages.VersioningConfiguration.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitPort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class MinioAdapter implements UploadKitPort {

    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @Override
    public void upload(MultipartFile dslZipFile, String dslJsonFile) {
        String bucketName = properties.getBucketName();
        try {
            minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
                .bucket(bucketName)
                .config(new VersioningConfiguration(Status.ENABLED, false))
                .build());
            checkBucketExistence(bucketName);

            InputStream zipFileInputStream = dslZipFile.getInputStream();
            String dslFileNameNoSuffix = Objects.requireNonNull(dslZipFile.getOriginalFilename()).replace(".zip", "");
            String dslFileDirPathAddr = properties.getObjectName() + LocalDate.now() + "/" + dslFileNameNoSuffix + "/";
            String zipFileObjectName = dslFileDirPathAddr + dslZipFile.getOriginalFilename();
            String zipJsonFileObjectName = dslFileDirPathAddr + dslFileNameNoSuffix + ".json";

            boolean objectExistence = checkObjectExistence(dslZipFile, bucketName, zipFileObjectName);

            ObjectWriteResponse dslZipFileWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(zipFileObjectName)
                .stream(zipFileInputStream, zipFileInputStream.available(), -1)
                .build());

            String zipFileUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(zipFileObjectName)
                    .expiry(5, TimeUnit.MINUTES)
                    .method(Method.GET)
                .build());

            log.warn("zipFileUrl:");
            log.warn(zipFileUrl);

            InputStream jsonFileInputStream = new ByteArrayInputStream(dslJsonFile.getBytes());
            ObjectWriteResponse dslJsonFileWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(zipJsonFileObjectName)
                .stream(jsonFileInputStream, jsonFileInputStream.available(), -1)
                .build());

            String jsonFileUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(zipJsonFileObjectName)
                .expiry(5, TimeUnit.MINUTES)
                .method(Method.GET)
                .build());

            log.warn("jsonFileUrl:");
            log.warn(jsonFileUrl);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean checkObjectExistence(MultipartFile dslZipFile, String bucketName, String objectName) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        }
    }

    private void checkBucketExistence(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .build());
        }
    }
}
