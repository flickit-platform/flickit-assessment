package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetenceAsMapByMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LevelCompetencePersistenceJpaAdapter implements
    LoadLevelCompetenceAsMapByMaturityLevelPort,
    DeleteLevelCompetencePort,
    CreateLevelCompetencePort,
    UpdateLevelCompetencePort {

    private final LevelCompetenceJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelJpaRepository;

    @Override
    public Map<String, Integer> loadByMaturityLevelId(Long maturityLevelId) {
        var levelCompetences = repository.findByMaturityLevelId(maturityLevelId);
        return levelCompetences.stream()
            .collect(Collectors.toMap(entity -> entity.getLevelCompetence().getTitle(),
                LevelCompetenceJpaEntity::getValue));
    }

    @Override
    public void delete(String competenceLevelTitle, Long maturityLevelId) {
        Long competenceLevelId = maturityLevelJpaRepository.findByTitle(competenceLevelTitle).getId();
        repository.delete(competenceLevelId, maturityLevelId);
    }

    @Override
    public Long persist(String levelCompetenceTitle, Integer value, String maturityLevelTitle) {
        LevelCompetenceJpaEntity entity = new LevelCompetenceJpaEntity(
            null,
            maturityLevelJpaRepository.findByTitle(maturityLevelTitle),
            maturityLevelJpaRepository.findByTitle(levelCompetenceTitle),
            value
        );
        return repository.save(entity).getId();
    }

    @Override
    public void update(Long competenceId, String competenceTitle, Integer value) {
        repository.update(
            competenceId,
            maturityLevelJpaRepository.findByTitle(competenceTitle).getId(),
            value);
    }
}
