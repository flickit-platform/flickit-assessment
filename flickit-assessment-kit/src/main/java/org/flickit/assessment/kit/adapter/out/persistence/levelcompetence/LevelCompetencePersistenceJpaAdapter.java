package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetencesByMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void delete(Long effectiveLevelId, Long maturityLevelId, Long kitId) {
        repository.delete(effectiveLevelId, maturityLevelId);
    }

    @Override
    public Long persist(Long effectiveLevelId, Integer value, String maturityLevelCode, Long kitId) {
        LevelCompetenceJpaEntity entity = new LevelCompetenceJpaEntity(
            null,
            maturityLevelJpaRepository.findByCodeAndAssessmentKitId(maturityLevelCode, kitId),
            maturityLevelJpaRepository.findByIdAndAssessmentKitId(effectiveLevelId, kitId),
            value
        );
        return repository.save(entity).getId();
    }

    @Override
    public void update(Long competenceId, Long effectiveLevelId, Integer value, Long kitId) {
        repository.update(
            competenceId,
            effectiveLevelId,
            value);
    }
}
