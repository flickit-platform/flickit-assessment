package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.FIND_MATURITY_LEVEL_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class LevelCompetencePersistenceJpaAdapter implements
    DeleteLevelCompetencePort,
    CreateLevelCompetencePort,
    UpdateLevelCompetencePort {

    private final LevelCompetenceJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelJpaRepository;

    @Override
    public void delete(Long affectedLevelId, Long maturityLevelId) {
        repository.delete(affectedLevelId, maturityLevelId);
    }

    @Override
    public Long persist(Long affectedLevelId, Long effectiveLevelId, int value, UUID createdBy) {
        LevelCompetenceJpaEntity entity = new LevelCompetenceJpaEntity(
            null,
            maturityLevelJpaRepository.findById(affectedLevelId).orElseThrow(() -> new ResourceNotFoundException(FIND_MATURITY_LEVEL_ID_NOT_FOUND)),
            maturityLevelJpaRepository.findById(effectiveLevelId).orElseThrow(() -> new ResourceNotFoundException(FIND_MATURITY_LEVEL_ID_NOT_FOUND)),
            value,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createdBy,
            createdBy
        );
        return repository.save(entity).getId();
    }

    @Override
    public void update(Long affectedLevelId, Long effectiveLevelId, Integer value, UUID lastModifiedBy) {
        repository.update(
            affectedLevelId,
            effectiveLevelId,
            value,
            LocalDateTime.now(),
            lastModifiedBy);
    }
}
