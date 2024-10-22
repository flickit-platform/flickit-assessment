package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslJsonPathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UpdateKitDslPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public Long create(String dslFilePath, String jsonFilePath, UUID createdBy) {
        var entity = toJpaEntity(dslFilePath, jsonFilePath, createdBy);
        entity.setId(sequenceGenerators.generateKitDslId());
        return repository.save(entity).getId();
    }

    @Override
    public String loadJsonPath(Long kitDslId) {
        KitDslJpaEntity kitDslEntity = repository.findById(kitDslId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND));
        return kitDslEntity.getJsonPath();
    }

    @Override
    public void update(Long id, Long kitId, UUID lastModifiedBy, LocalDateTime lastModificationTime) {
        repository.removeKitId(kitId, lastModifiedBy, lastModificationTime);

        repository.updateById(id, kitId, lastModifiedBy, lastModificationTime);
    }

    @Override
    public Optional<String> loadDslFilePath(Long kitId) {
        return repository.findDslPathByKitId(kitId);
    }
}
