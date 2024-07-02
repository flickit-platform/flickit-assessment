package org.flickit.assessment.kit.adapter.out.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.config.MinioConfigProperties;
import org.flickit.assessment.kit.application.port.out.kitdsl.UploadKitDslToFileStoragePort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.minio.LoadKitDSLJsonFilePort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.flickit.assessment.common.error.ErrorMessageKey.FILE_STORAGE_FILE_NOT_FOUND;
import static org.flickit.assessment.kit.adapter.out.minio.MinioConstants.*;


@Component
@AllArgsConstructor
public class MinioAdapter implements
    UploadKitDslToFileStoragePort,
    LoadKitDSLJsonFilePort,
    CreateFileDownloadLinkPort {

    public static final String SLASH = "/";
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @SneakyThrows
    @Override
    public UploadKitDslToFileStoragePort.Result uploadKitDsl(MultipartFile dslZipFile, String dslJsonFile) {
        String bucketName = properties.getBucketNames().getDsl();
        UUID uniqueObjectName = UUID.randomUUID();
        String dslFileObjectName = uniqueObjectName + DSL_FILE_NAME;
        String dslJsonObjectName = uniqueObjectName + DSL_JSON_NAME;

        InputStream zipFileInputStream = dslZipFile.getInputStream();
        writeFile(bucketName, dslFileObjectName, zipFileInputStream, dslZipFile.getContentType());

        InputStream jsonFileInputStream = new ByteArrayInputStream(dslJsonFile.getBytes(UTF_8));
        writeFile(bucketName, dslJsonObjectName, jsonFileInputStream, JSON_CONTENT_TYPE);

        String dslFilePath = bucketName + SLASH + dslFileObjectName;
        String dslJsonPath = bucketName + SLASH + dslJsonObjectName;
        return new UploadKitDslToFileStoragePort.Result(dslFilePath, dslJsonPath);
    }

    @SneakyThrows
    private void writeFile(String bucketName, String fileObjectName, InputStream fileInputStream, @Nullable String contentType) {
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(fileObjectName)
            .contentType(getContentType(contentType))
            .stream(fileInputStream, fileInputStream.available(), -1)
            .build());
    }

    private String getContentType(@Nullable String contentType) {
        return (contentType != null) ? contentType : "application/octet-stream";
    }

    @SneakyThrows
    @Override
    public String loadDslJson(String dslJsonFullPath) {
        String bucketName = dslJsonFullPath.substring(0, dslJsonFullPath.indexOf(SLASH));
        String objectName = dslJsonFullPath.substring(dslJsonFullPath.indexOf(SLASH));

        checkFileExistence(bucketName, objectName);

        InputStream stream = minioClient
            .getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());

        return new String(stream.readAllBytes(), UTF_8);
    }

    @SneakyThrows
    @Override
    public String createDownloadLink(String filePath, Duration expiryDuration) {
        if(filePath == null || filePath.isBlank())
            return null;

        String bucketName = filePath.substring(0, filePath.indexOf(SLASH));
        String objectName = filePath.substring(filePath.indexOf(SLASH) + 1);

        try {
            checkFileExistence(bucketName, objectName);
        } catch (ResourceNotFoundException e) {
            return null;
        }

        String downloadUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(bucketName)
            .object(objectName)
            .expiry((int) expiryDuration.getSeconds(), TimeUnit.SECONDS)
            .method(Method.GET)
            .build());

        return downloadUrl.replace(properties.getUrl(), properties.getApi());
    }

    @SneakyThrows
    private void checkFileExistence(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(FILE_STORAGE_FILE_NOT_FOUND);
        }
    }
}
