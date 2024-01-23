package org.flickit.assessment.kit.application.port.out.assessmentkitdsl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.flickit.assessment.kit.config.MinioConfigProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface LoadDslFilePathPort {

    String loadDslFilePath(Long kitId, Duration expiryDuration);
}
