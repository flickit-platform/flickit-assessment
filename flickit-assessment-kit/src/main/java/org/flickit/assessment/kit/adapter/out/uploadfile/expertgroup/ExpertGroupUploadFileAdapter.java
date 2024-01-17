package org.flickit.assessment.kit.adapter.out.uploadfile.expertgroup;

import io.minio.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@AllArgsConstructor
public class ExpertGroupUploadFileAdapter implements
    UploadExpertGroupPicturePort {
    @Override
    @SneakyThrows
    public Result upload(MultipartFile pictureFile){
            MinioClient minioClient =
                MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
            boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            ObjectWriteResponse ifo = minioClient.uploadObject(
                UploadObjectArgs.builder()
                    .bucket("asiatrip")
                    .object("asiaphotos-2015.zip")
                    .filename("/home/maziyar/Documents/edqp.pdf")
                    .build());
            String fullPath = "http://127.0.0.1:9000" + "/" + ifo.bucket() + "/" + ifo.object();

        return new Result(fullPath);
    }
}
