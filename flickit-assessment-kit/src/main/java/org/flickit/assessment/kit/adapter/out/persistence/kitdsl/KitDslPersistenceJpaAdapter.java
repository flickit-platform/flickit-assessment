package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaRepository;
import org.flickit.assessment.kit.application.port.out.kitdsl.*;
import org.springframework.stereotype.Component;

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
    LoadDslFilePathPort,
    CheckIsMemberPort {

    private final KitDslJpaRepository repository;

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
    public Optional<String> loadDslFilePath(Long kitId) {
        return repository.findDslPathByKitId(kitId);
    }

    @Override
    public Boolean checkIsMemberByKitId(long kitId, UUID currentUserId) {
        return repository.checkIsMemberByKitId(kitId, currentUserId);
    }
}
