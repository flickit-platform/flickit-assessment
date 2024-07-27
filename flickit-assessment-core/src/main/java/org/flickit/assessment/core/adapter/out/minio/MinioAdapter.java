package org.flickit.assessment.core.adapter.out.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.evidenceattachment.UploadEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteEvidenceAttachmentFilePort;
import org.flickit.assessment.core.application.port.out.minio.DownloadFilePort;
import org.flickit.assessment.data.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.flickit.assessment.common.error.ErrorMessageKey.FILE_STORAGE_FILE_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_NOT_FOUND;

@Component("coreMinioAdapter")
@AllArgsConstructor
public class MinioAdapter implements
    CreateFileDownloadLinkPort,
    UploadEvidenceAttachmentPort,
    DeleteEvidenceAttachmentFilePort,
    DownloadFilePort {

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
    public void deleteEvidenceAttachmentFile(String path) {
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

    @SneakyThrows
    @Override
    public InputStream downloadFile(String fileLink) {
        URL pictureUrl = new URL(fileLink);

        try (ReadableByteChannel readableByteChannel = Channels.newChannel(pictureUrl.openStream());
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (readableByteChannel.read(buffer) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_NOT_FOUND);
        }
    }
}
