package org.flickit.assessment.users.adapter.out.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.data.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.flickit.assessment.users.adapter.out.minio.MinioConstants.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.FILE_STORAGE_FILE_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.FILE_PROPERTIES_MAX_FILE_SIZE_EXCEEDED;

@Component("usersMinioAdapter")
@AllArgsConstructor
public class MinioAdapter implements
    UploadExpertGroupPicturePort,
    CreateFileDownloadLinkPort {

    public static final String SLASH = "/";
    public static final String DOT = ".";
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;
    private final FileProperties fileProperties;


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
    public String uploadPicture(MultipartFile pictureFile) {
        if (pictureFile.getSize() > fileProperties.getPictureMaxSize().toBytes())
            throw new ValidationException(FILE_PROPERTIES_MAX_FILE_SIZE_EXCEEDED);

        String bucketName = properties.getBucketNames().getAvatar();
        UUID uniqueDir = UUID.randomUUID();

        String extension = "";
        if (pictureFile.getOriginalFilename() != null)
            extension = pictureFile.getOriginalFilename().substring(pictureFile.getOriginalFilename().indexOf(DOT));

        String objectName = uniqueDir + PIC_FILE_NAME + extension;
        writeFile(bucketName, objectName, pictureFile.getInputStream(), pictureFile.getContentType());
        return bucketName + SLASH + objectName;
    }

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
}
