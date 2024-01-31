package org.flickit.assessment.kit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.minio")
public class MinioConfigProperties {

    private String url;
    private String api;
    private int port;
    private String accessKey;
    private String accessSecret;
    private String bucketName;
    private String objectName;
    private Boolean secure;
}
