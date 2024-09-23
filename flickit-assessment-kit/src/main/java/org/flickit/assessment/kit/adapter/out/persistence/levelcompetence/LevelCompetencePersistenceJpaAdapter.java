package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LevelCompetencePersistenceJpaAdapter implements
    DeleteLevelCompetencePort,
    CreateLevelCompetencePort,
    UpdateLevelCompetencePort {

    private final LevelCompetenceJpaRepository repository;

    @Override
    public void delete(Long affectedLevelId, Long maturityLevelId, Long kitVersionId) {
        repository.delete(affectedLevelId, maturityLevelId, kitVersionId);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public Long persist(Long affectedLevelId, Long effectiveLevelId, int value, Long kitVersionId, UUID createdBy) {
        LevelCompetenceJpaEntity entity = new LevelCompetenceJpaEntity(
            null,
            affectedLevelId,
            effectiveLevelId,
            value,
            kitVersionId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createdBy,
            createdBy
        );
        return repository.save(entity).getId();
    }

    @Override
    public void update(Long affectedLevelId, Long effectiveLevelId, Long kitVersionId, Integer value, UUID lastModifiedBy) {
        repository.update(
            affectedLevelId,
            effectiveLevelId,
            kitVersionId,
            value,
            LocalDateTime.now(), lastModifiedBy);
    }

    @Override
    public void updateInfo(Param param) {
        repository.updateValue(param.id(),
            param.value(),
            param.lastModifiedBy(),
            param.lastModificationTime());
    }
}
