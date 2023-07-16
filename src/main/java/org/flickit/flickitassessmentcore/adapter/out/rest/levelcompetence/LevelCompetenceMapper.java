package org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence;

import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;

import java.util.List;

public class LevelCompetenceMapper {

    public static LoadLevelCompetenceByMaturityLevelPort.Result toResult(List<LevelCompetenceRestAdapter.LevelCompetenceDto> dtos) {
        return new LoadLevelCompetenceByMaturityLevelPort.Result(
            dtos.stream().
                map(LevelCompetenceMapper::toDomainModel)
                .toList()
        );
    }

    private static LevelCompetence toDomainModel(LevelCompetenceRestAdapter.LevelCompetenceDto dto) {
        return new LevelCompetence(
            dto.id(),
            dto.maturityLevelId(),
            dto.value(),
            dto.maturityLevelCompetenceId()
        );
    }
}
