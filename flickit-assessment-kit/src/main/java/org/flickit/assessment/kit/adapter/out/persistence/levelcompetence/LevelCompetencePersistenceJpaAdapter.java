package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.LEVEL_COMPETENCE_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_LEVEL_COMPETENCE_ID_NOT_FOUND;

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
    public void delete(Long levelCompetenceId, Long kitVersionId) {
        if (repository.existsByIdAndKitVersionId(levelCompetenceId, kitVersionId))
            repository.deleteByIdAndKitVersionId(levelCompetenceId, kitVersionId);
        else
            throw new ResourceNotFoundException(DELETE_LEVEL_COMPETENCE_ID_NOT_FOUND);
    }

    @Override
    public Long persist(Long affectedLevelId, Long effectiveLevelId, int value, Long kitVersionId, UUID createdBy) {
        LevelCompetenceJpaEntity entity = new LevelCompetenceJpaEntity(
            null,
            kitVersionId,
            affectedLevelId,
            effectiveLevelId,
            value,
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
    public void updateById(long id, long kitVersionId, int value, UUID lastModifiedBy) {
        if (!repository.existsByIdAndKitVersionId(id, kitVersionId))
            throw new ResourceNotFoundException(LEVEL_COMPETENCE_ID_NOT_FOUND);

        repository.updateById(id, kitVersionId, value, LocalDateTime.now(), lastModifiedBy);
    }
}
