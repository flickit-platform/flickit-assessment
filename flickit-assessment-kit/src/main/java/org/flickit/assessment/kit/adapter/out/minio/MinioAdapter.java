package org.flickit.assessment.kit.adapter.out.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.VersioningConfiguration;
import io.minio.messages.VersioningConfiguration.Status;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateDslDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UploadKitDslToFileStoragePort;
import org.flickit.assessment.kit.application.port.out.minio.LoadKitDSLJsonFilePort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_BY_DSL_KIT_DSL_FILE_NOT_FOUND;

@Component
@AllArgsConstructor
public class MinioAdapter implements
    UploadKitDslToFileStoragePort,
    LoadKitDSLJsonFilePort,
    UploadExpertGroupPicturePort,
    CreateDslDownloadLinkPort {

    public static final String SLASH = "/";
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @SneakyThrows
    @Override
    public UploadKitDslToFileStoragePort.Result uploadKitDsl(MultipartFile dslZipFile, String dslJsonFile) {
        String bucketName = properties.getBucketName();
        String dslFileNameNoSuffix = Objects.requireNonNull(dslZipFile.getOriginalFilename()).replace(".zip", "");
        String dslFileDirPathAddr = properties.getObjectName() + LocalDate.now() + SLASH + dslFileNameNoSuffix + SLASH;
        String dslFileObjectName = dslFileDirPathAddr + dslZipFile.getOriginalFilename();
        String jsonFileObjectName = dslFileDirPathAddr + dslFileNameNoSuffix + ".json";

        createBucket(bucketName);
        setBucketVersioning(bucketName);

        InputStream zipFileInputStream = dslZipFile.getInputStream();
        String zipFileVersionId = writeFile(bucketName, dslFileObjectName, zipFileInputStream).versionId();

        InputStream jsonFileInputStream = new ByteArrayInputStream(dslJsonFile.getBytes());
        String jsonFileVersionId = writeFile(bucketName, jsonFileObjectName, jsonFileInputStream).versionId();

        return new UploadKitDslToFileStoragePort.Result(
            dslFileObjectName + SLASH + zipFileVersionId,
            jsonFileObjectName + SLASH + jsonFileVersionId);
    }

    @SneakyThrows
    private void setBucketVersioning(String bucketName) {
        minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
            .bucket(bucketName)
            .config(new VersioningConfiguration(Status.ENABLED, false))
            .build());
    }

    @SneakyThrows
    private ObjectWriteResponse writeFile(String bucketName, String fileObjectName, InputStream fileInputStream) {
        return minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(fileObjectName)
            .stream(fileInputStream, fileInputStream.available(), -1)
            .build());
    }

    @SneakyThrows
    @Override
    public String loadDslJson(String dslJsonFullPath) {
        String path = dslJsonFullPath.substring(0, dslJsonFullPath.lastIndexOf(SLASH));
        String versionId = dslJsonFullPath.substring(dslJsonFullPath.lastIndexOf(SLASH) + 1);
        String bucketName = properties.getBucketName();

        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .versionId(versionId)
                .build());
        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_FILE_NOT_FOUND);
        }

        InputStream stream = minioClient
            .getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .versionId(versionId)
                .build());

        return new String(stream.readAllBytes());
    }

    @Override
    @SneakyThrows
    public String uploadPicture(MultipartFile pictureFile) {
        createBucket(properties.getBucketName());
        setBucketVersioning(properties.getBucketName());

        var result = writeFile(properties.getBucketName(), pictureFile.getOriginalFilename(), pictureFile.getInputStream());
        return result.bucket() + "/" + result.object();
    }

    @SneakyThrows
    private void createBucket(String bucketName) {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .build());
        }
    }

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
