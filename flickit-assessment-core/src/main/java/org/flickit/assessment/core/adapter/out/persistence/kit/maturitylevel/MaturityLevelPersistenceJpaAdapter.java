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

@Component("CoreMaturityLevelPersistenceJpaAdapter")
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    LoadMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<MaturityLevel> loadByKitId(Long kitId) {
        return repository.findAllByAssessmentKitId(kitId).stream()
            .map(x -> new MaturityLevel(
                x.getId(),
                x.getIndex(),
                x.getValue(),
                null
            ))
            .toList();
    }

    @Override
    public List<MaturityLevel> loadByKitIdWithCompetences(Long kitId) {
        List<MaturityJoinCompetenceView> results = repository.findAllByKitIdWithCompetence(kitId);

        Map<Long, List<MaturityJoinCompetenceView>> collect = results.stream()
            .collect(Collectors.groupingBy(x -> x.getMaturityLevel().getId()));

        return collect.values().stream().map(result -> {

            MaturityLevelJpaEntity levelEntity = result.stream().findFirst().get().getMaturityLevel();

            List<LevelCompetence> competences = result.stream()
                .map(MaturityJoinCompetenceView::getLevelCompetence)
                .filter(Objects::nonNull)
                .map(entity -> new LevelCompetence(entity.getId(), entity.getValue(), entity.getEffectiveLevel().getId()))
                .toList();

            return new MaturityLevel(
                levelEntity.getId(),
                levelEntity.getIndex(),
                levelEntity.getValue(),
                competences
            );
        }).toList();
    }
}
