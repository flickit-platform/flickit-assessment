package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaRepository;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadDslJsonPathPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.UpdateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.flickit.assessment.kit.config.MinioConfigProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.KitDslMapper.toJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitDslPersistenceJpaAdapter implements
    CreateKitDslPort,
    LoadDslJsonPathPort,
    UpdateKitDslPort,
    LoadDslFilePathPort {

    private final KitDslJpaRepository repository;
    private final MinioClient minioClient;
    private final MinioConfigProperties properties;

    @Override
    public Long create(String dslFilePath, String jsonFilePath) {
        return repository.save(toJpaEntity(dslFilePath, jsonFilePath)).getId();
    }

    @Override
    public String loadJsonPath(Long kitDslId) {
        KitDslJpaEntity kitDslEntity = repository.findById(kitDslId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND));
        return kitDslEntity.getJsonPath();
    }

    @Override
    public void update(Long id, Long kitId) {
        repository.updateById(id, kitId);
    }

    @SneakyThrows
    @Override
    public String loadDslFilePath(Long kitId, Duration expiryDuration) {
        String filePath = repository.findDslPathByKitId(kitId);

        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(properties.getBucketName())
            .object(filePath)
            .expiry((int) expiryDuration.getSeconds(), TimeUnit.SECONDS)
            .method(Method.GET)
            .build());
    }
}
