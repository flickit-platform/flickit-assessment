package org.flickit.assessment.data.config;

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
    private BucketNames bucketNames;
    private Boolean secure;

    @Getter
    @Setter
    public static class BucketNames {

        private String dsl;
        private String avatar;
        private String attachment;
        private String report;
    }
}


