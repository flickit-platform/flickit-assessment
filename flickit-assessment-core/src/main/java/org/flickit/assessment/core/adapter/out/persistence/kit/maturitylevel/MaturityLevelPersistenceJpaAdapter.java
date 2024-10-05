package org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.LevelCompetence;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityJoinCompetenceView;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper.mapToDomainModel;

@Component("coreMaturityLevelPersistenceJpaAdapter")
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    LoadMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<MaturityLevel> loadByKitVersionId(Long kitVersionId) {
        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId, null).stream()
            .map(levelEntity -> mapToDomainModel(levelEntity, null))
            .toList();
    }

    public List<MaturityLevel> loadByKitVersionIdWithCompetences(Long kitVersionId) {
        List<MaturityJoinCompetenceView> results = repository.findAllByKitVersionIdWithCompetence(kitVersionId);

        Map<Long, List<MaturityJoinCompetenceView>> collect = results.stream()
            .collect(Collectors.groupingBy(x -> x.getMaturityLevel().getId()));

        return collect.values().stream().map(result -> {
            MaturityLevelJpaEntity levelEntity = result.stream()
                .findFirst()
                .orElseThrow() // Can't happen
                .getMaturityLevel();

            List<LevelCompetence> competences = result.stream()
                .map(MaturityJoinCompetenceView::getLevelCompetence)
                .filter(Objects::nonNull)
                .map(MaturityLevelPersistenceJpaAdapter::mapToCompetenceDomainModel)
                .toList();

            return mapToDomainModel(levelEntity, competences);
        }).toList();
    }

    private static LevelCompetence mapToCompetenceDomainModel(LevelCompetenceJpaEntity entity) {
        return new LevelCompetence(
            entity.getId(),
            entity.getValue(),
            entity.getEffectiveLevelId());
    }
}
