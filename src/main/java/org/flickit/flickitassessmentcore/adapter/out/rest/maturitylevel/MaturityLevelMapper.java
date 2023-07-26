package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import org.flickit.flickitassessmentcore.domain.LevelCompetence;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

public class MaturityLevelMapper {

    static MaturityLevel toDomainModel(MaturityLevelRestAdapter.MaturityLevelDto dto) {
        return new MaturityLevel(
            dto.id(),
            dto.title(),
            dto.value(),
            dto.levelCompetences().stream()
                .map(lc -> new LevelCompetence(null, null, dto.value(), lc.maturityLevelCompetenceId()))
                .toList()
        );
    }
}
