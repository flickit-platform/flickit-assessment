package org.flickit.assessment.kit.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.levelcompetence.MaturityLevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.levelcompetence.MaturityLevelCompetenceJpaRepository;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.LoadLevelCompetenceAsMapByMaturityLevelPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MaturityLevelCompetencePersistenceJpaAdapter implements
    LoadLevelCompetenceAsMapByMaturityLevelPort {

    private final MaturityLevelCompetenceJpaRepository repository;

    @Override
    public Map<String, Integer> loadByMaturityLevelId(Long maturityLevelId) {
        var levelCompetences = repository.findByMaturityLevelId(maturityLevelId);
        return levelCompetences.stream()
            .collect(Collectors.toMap(entity -> entity.getMaturityLevel().getTitle(),
                MaturityLevelCompetenceJpaEntity::getValue));
    }
}
