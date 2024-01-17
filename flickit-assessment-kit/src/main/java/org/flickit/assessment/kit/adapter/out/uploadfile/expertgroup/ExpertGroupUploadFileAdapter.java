package org.flickit.assessment.kit.adapter.out.uploadfile.expertgroup;

import io.minio.*;
import io.minio.messages.VersioningConfiguration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Component
@AllArgsConstructor
public class ExpertGroupUploadFileAdapter implements
    UploadExpertGroupPicturePort {

    private final MinioClient minioClient;
    private final MinioConfigProperties properties;
    @Override
    @SneakyThrows
    public String upload(MultipartFile pictureFile){
        createBucket(properties.getBucketName());
        setBucketVersioning(properties.getBucketName());

        return writeFile(properties.getBucketName(), pictureFile.getOriginalFilename(), pictureFile.getInputStream());
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
    private String writeFile(String bucketName, String pictureName, InputStream pictureStream) {
        var result = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(pictureName)
                .stream(pictureStream, pictureStream.available(), -1)
                .build());
        return properties.getUrl()+"/"+result.bucket()+"/"+result.object();
    }

    @SneakyThrows
    private void setBucketVersioning(String bucketName) {
        minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
            .bucket(bucketName)
            .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false))
            .build());
    }
}
