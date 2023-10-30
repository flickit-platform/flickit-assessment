package org.flickit.assessment.core.adapter.out.rest.levelcompetence;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.application.domain.LevelCompetence;

public record LevelCompetenceDto(Long id,
                                 Integer value,
                                 @JsonProperty("maturity_level_id")
                                 Long maturityLevelId) {

    public LevelCompetence dtoToDomain() {
        return new LevelCompetence(id, value, maturityLevelId);
    }
}


