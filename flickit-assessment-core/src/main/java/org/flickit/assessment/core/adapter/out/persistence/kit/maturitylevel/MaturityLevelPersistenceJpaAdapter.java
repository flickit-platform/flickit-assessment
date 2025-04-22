package org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.LevelCompetence;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.maturitylevel.CountMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityJoinCompetenceView;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper.mapToDomainModel;

@Component("coreMaturityLevelPersistenceJpaAdapter")
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    LoadMaturityLevelsPort,
    CountMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<MaturityLevel> loadByKitVersionId(Long kitVersionId) {
        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
    }

    public List<MaturityLevel> loadByKitVersionIdWithCompetences(Long kitVersionId) {
        var views = repository.findAllByKitVersionIdWithCompetence(kitVersionId);

        var groupedByLevelId = views.stream()
            .collect(Collectors.groupingBy(view -> view.getMaturityLevel().getId()));

        return groupedByLevelId.values().stream()
            .map(group -> {
                var levelEntity = group.getFirst().getMaturityLevel();

                var competences = group.stream()
                    .map(MaturityJoinCompetenceView::getLevelCompetence)
                    .filter(Objects::nonNull)
                    .map(MaturityLevelPersistenceJpaAdapter::mapToCompetenceDomainModel)
                    .toList();

                var level = mapToDomainModel(levelEntity);
                level.setLevelCompetences(competences);

                return level;
            })
            .toList();
    }

    private static LevelCompetence mapToCompetenceDomainModel(LevelCompetenceJpaEntity entity) {
        return new LevelCompetence(
            entity.getId(),
            entity.getValue(),
            entity.getEffectiveLevelId());
    }

    @Override
    public int count(long kitVersionId) {
        return repository.countByKitVersionId(kitVersionId);
    }
}
