package org.flickit.assessment.scenario.data.config;

import io.minio.MinioClient;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class MinioContainerConnectionTest {

    @Autowired
    private MinioClient minioClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        PostgresTestContainerHolder.setProperties(registry);
        MinioTestContainerHolder.setProperties(registry);
    }

    @SneakyThrows
    @Test
    @DisplayName("The application should connect to minio test container")
    void testMinioConnection() {
        var minIOContainer = MinioTestContainerHolder.getInstance();
        assertTrue(minIOContainer.isRunning());
        List<Bucket> buckets = minioClient.listBuckets();
        assertEquals(4, buckets.size());
        List<String> bucketNames = buckets.stream().map(Bucket::name).toList();
        assertTrue(bucketNames.containsAll(Set.of("dsl", "avatar", "attachment", "report")));
    }
}
