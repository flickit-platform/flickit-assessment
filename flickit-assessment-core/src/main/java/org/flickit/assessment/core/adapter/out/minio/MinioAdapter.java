package org.flickit.assessment.core.adapter.out.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.evidenceattachment.UploadEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.core.application.port.out.minio.UploadAssessmentAnalysisInputFilePort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.flickit.assessment.data.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.flickit.assessment.common.error.ErrorMessageKey.FILE_STORAGE_FILE_NOT_FOUND;

@Component("coreMinioAdapter")
@AllArgsConstructor
public class MinioAdapter implements
    CreateFileDownloadLinkPort,
    UploadEvidenceAttachmentPort,
    DeleteFilePort,
    UploadAttributeScoresFilePort,
    UploadAssessmentAnalysisInputFilePort {

    public static final String SLASH = "/";
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @SneakyThrows
    @Override
    public String createDownloadLink(String filePath, Duration expiryDuration) {
        if (filePath == null || filePath.isBlank())
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

    @SneakyThrows
    @Override
    public String uploadAttachment(MultipartFile pictureFile) {
        String bucketName = properties.getBucketNames().getAttachment();
        UUID uniqueDir = UUID.randomUUID();

        String objectName = uniqueDir + SLASH + pictureFile.getOriginalFilename();
        writeFile(bucketName, objectName, pictureFile.getInputStream(), pictureFile.getContentType());
        return bucketName + SLASH + objectName;
    }

    @SneakyThrows
    private void writeFile(String bucketName, String fileObjectName, InputStream fileInputStream, String contentType) {
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(fileObjectName)
            .contentType(contentType)
            .stream(fileInputStream, fileInputStream.available(), -1)
            .build());
    }

    @SneakyThrows
    @Override
    public void deleteFile(String path) {
        String bucketName = path.replaceFirst("/.*", "");
        String objectName = path.replaceFirst("^" + bucketName + "/", "");

        checkFileExistence(bucketName, objectName);

        String latestVersionId = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(objectName)
                .includeVersions(true)
                .build()
        ).iterator().next().get().versionId();

        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(bucketName)
            .object(objectName)
            .versionId(latestVersionId)
            .build());
    }

    @Override
    public String uploadExcel(InputStream content, String fileName) {
        String bucketName = properties.getBucketNames().getReport();
        UUID uniqueDir = UUID.randomUUID();

        String objectName = uniqueDir + SLASH + fileName;
        writeFile(bucketName, objectName, content, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return bucketName + SLASH + objectName;
    }

    @SneakyThrows
    @Override
    public String uploadAssessmentAnalysisInputFile(MultipartFile inputFile) {
        String bucketName = properties.getBucketNames().getReport();
        UUID uniqueDir = UUID.randomUUID();

        String objectName = uniqueDir + SLASH + inputFile.getOriginalFilename();
        writeFile(bucketName, objectName, inputFile.getInputStream(), inputFile.getContentType());
        return bucketName + SLASH + objectName;
    }
}
