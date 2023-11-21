package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetencesByMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.FIND_MATURITY_LEVEL_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class LevelCompetencePersistenceJpaAdapter implements
    LoadLevelCompetencesByMaturityLevelPort,
    DeleteLevelCompetencePort,
    CreateLevelCompetencePort,
    UpdateLevelCompetencePort {

    private final LevelCompetenceJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelJpaRepository;

    @Override
    public List<MaturityLevelCompetence> loadByMaturityLevelId(Long maturityLevelId) {
        var levelCompetences = repository.findByMaturityLevelId(maturityLevelId);
        return levelCompetences.stream()
            .map(MaturityLevelCompetenceMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public void delete(Long affectedLevelId, Long maturityLevelId) {
        repository.delete(affectedLevelId, maturityLevelId);
    }

    @Override
    public Long persist(Long affectedLevelId, Long effectiveLevelId, int value) {
        LevelCompetenceJpaEntity entity = new LevelCompetenceJpaEntity(
            null,
            maturityLevelJpaRepository.findById(affectedLevelId).orElseThrow(() -> new ResourceNotFoundException(FIND_MATURITY_LEVEL_ID_NOT_FOUND)),
            maturityLevelJpaRepository.findById(effectiveLevelId).orElseThrow(() -> new ResourceNotFoundException(FIND_MATURITY_LEVEL_ID_NOT_FOUND)),
            value
        );
        return repository.save(entity).getId();
    }

    @Override
    public void update(Long affectedLevelId, Long effectiveLevelId, Integer value) {
        repository.update(
            affectedLevelId,
            effectiveLevelId,
            value);
    }
}
