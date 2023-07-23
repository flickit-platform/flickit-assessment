package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence.LevelCompetenceMapper;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.List;

public class MaturityLevelMapper {
    public static LoadMaturityLevelByKitPort.Result toResult(List<MaturityLevelRestAdapter.MaturityLevelDto> dto) {
        return new LoadMaturityLevelByKitPort.Result(
            dto.stream().
                map(MaturityLevelMapper::toDomainModel).
                toList()
        );
    }

    private static MaturityLevel toDomainModel(MaturityLevelRestAdapter.MaturityLevelDto dto) {
        return new MaturityLevel(
            dto.id(),
            dto.title(),
            dto.value(),
            null,
            dto.levelCompetences().stream()
                .map(lc -> new LevelCompetence(null, null, dto.value(), lc.maturityLevelCompetenceId()))
                .toList()
        );
    }
}
