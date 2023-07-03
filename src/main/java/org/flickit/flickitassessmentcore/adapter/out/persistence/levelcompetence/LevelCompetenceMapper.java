package org.flickit.flickitassessmentcore.adapter.out.persistence.levelcompetence;

import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;

import java.util.List;
import java.util.stream.Collectors;

public class LevelCompetenceMapper {

    public static LoadLevelCompetenceByMaturityLevelPort.Result toResult(List<LevelCompetencePersistenceJpaAdapter.LevelCompetenceDto> dtos) {
        return new LoadLevelCompetenceByMaturityLevelPort.Result(
            dtos.stream().
                map(LevelCompetenceMapper::toDomainModel)
                .collect(Collectors.toSet())
        );
    }

    private static LevelCompetence toDomainModel(LevelCompetencePersistenceJpaAdapter.LevelCompetenceDto dto) {
        return new LevelCompetence(
            dto.id(),
            dto.maturityLevelId(),
            dto.value(),
            dto.maturityLevelCompetenceId()
        );
    }
}
