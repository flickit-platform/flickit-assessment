package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity.EntityId;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.out.maturitylevel.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.maturitylevel.MaturityLevelMapper.mapToJpaEntityToPersist;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    CreateMaturityLevelPort,
    DeleteMaturityLevelPort,
    UpdateMaturityLevelPort,
    LoadMaturityLevelsPort,
    LoadAttributeMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public Long persist(MaturityLevel level, Long kitVersionId, UUID createdBy) {
        return repository.save(mapToJpaEntityToPersist(level, kitVersionId, createdBy)).getId();
    }

    @Override
    public void delete(Long id, Long kitVersionId) {
        repository.deleteById(new MaturityLevelJpaEntity.EntityId(id, kitVersionId));
    }

    @Override
    public void update(List<MaturityLevel> maturityLevels, Long kitVersionId, UUID lastModifiedBy) {
        Map<EntityId, MaturityLevel> idToModel = maturityLevels.stream()
            .collect(Collectors.toMap(
                ml -> new EntityId(ml.getId(), kitVersionId),
                ml -> ml
            ));
        List<MaturityLevelJpaEntity> entities = repository.findAllById(idToModel.keySet());
        entities.forEach(x -> {
            MaturityLevel newLevel = idToModel.get(new EntityId(x.getId(), kitVersionId));
            x.setIndex(newLevel.getIndex());
            x.setTitle(newLevel.getTitle());
            x.setValue(newLevel.getValue());
            x.setLastModificationTime(LocalDateTime.now());
            x.setLastModifiedBy(lastModifiedBy);
        });
        repository.saveAll(entities);
        repository.flush();
    }

    @Override
    public List<MaturityLevel> loadByKitVersionId(Long kitVersionId) {
        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public List<LoadAttributeMaturityLevelsPort.Result> loadAttributeLevels(long attributeId, long kitVersionId) {
        return repository.loadAttributeLevels(attributeId, kitVersionId).stream()
            .map(e -> new LoadAttributeMaturityLevelsPort.Result(e.getId(), e.getTitle(), e.getIndex(), e.getQuestionCount()))
            .toList();
    }
}
