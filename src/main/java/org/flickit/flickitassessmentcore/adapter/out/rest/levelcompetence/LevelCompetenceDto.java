package org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.domain.calculate.LevelCompetence;

public record LevelCompetenceDto(Long id,
                                 Integer value,
                                 @JsonProperty("maturity_level_id")
                                 Long maturityLevelId) {

    public LevelCompetence dtoToDomain() {
        return LevelCompetence.builder()
            .id(id)
            .value(value)
            .maturityLevelId(maturityLevelId)
            .build();
    }
}


