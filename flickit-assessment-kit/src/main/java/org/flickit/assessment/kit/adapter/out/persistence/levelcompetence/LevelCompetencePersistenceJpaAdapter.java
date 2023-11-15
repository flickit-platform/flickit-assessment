package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetenceAsMapByMaturityLevelPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LevelCompetencePersistenceJpaAdapter implements
    LoadLevelCompetenceAsMapByMaturityLevelPort {

    private final LevelCompetenceJpaRepository repository;

    @Override
    public Map<String, Integer> loadByMaturityLevelId(Long maturityLevelId) {
        var levelCompetences = repository.findByMaturityLevelId(maturityLevelId);
        return levelCompetences.stream()
            .collect(Collectors.toMap(entity -> entity.getMaturityLevel().getTitle(),
                LevelCompetenceJpaEntity::getValue));
    }
}
