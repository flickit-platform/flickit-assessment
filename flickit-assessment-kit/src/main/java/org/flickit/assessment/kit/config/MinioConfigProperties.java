package org.flickit.assessment.kit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("minio")
public class MinioConfigProperties {

    private String url = "https://flickit-test-cdn.darkube.app:9000";
    private int port = 9000;
    private String accessKey = "test";
    private String accessSecret = "test";
    private String bucketName = "media";
    private String objectName = "assessment_kit/dsl";
    private Boolean secure = true;
}
