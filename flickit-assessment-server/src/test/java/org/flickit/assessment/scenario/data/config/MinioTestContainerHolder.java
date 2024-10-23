package org.flickit.assessment.scenario.data.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;

public class MinioTestContainerHolder {

    @Container
    private static final MinIOContainer MINIO_CONTAINER;

    static {
        MINIO_CONTAINER = new MinIOContainer("minio/minio:latest")
            .withUserName("minioadmin")
            .withPassword("minioadmin");

        MINIO_CONTAINER.start();
    }

    public static MinIOContainer getInstance() {
        return MINIO_CONTAINER;
    }

    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("app.minio.url", MINIO_CONTAINER::getS3URL);
        registry.add("app.minio.api", MINIO_CONTAINER::getS3URL);
        registry.add("app.minio.port", () -> MINIO_CONTAINER.getMappedPort(9000));
        registry.add("app.minio.access-key", MINIO_CONTAINER::getUserName);
        registry.add("app.minio.access-secret", MINIO_CONTAINER::getPassword);
        registry.add("app.minio.init-buckets", () -> true);
    }
}