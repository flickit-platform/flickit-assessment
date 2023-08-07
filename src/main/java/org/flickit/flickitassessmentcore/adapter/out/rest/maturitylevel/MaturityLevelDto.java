package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence.LevelCompetenceDto;
import org.flickit.flickitassessmentcore.domain.calculate.MaturityLevel;

import java.util.List;

public record MaturityLevelDto(Long id,
                               @JsonProperty("value")
                               Integer level,
                               @JsonProperty("level_competences")
                               List<LevelCompetenceDto> levelCompetences) {

    public MaturityLevel dtoToDomain() {
        return MaturityLevel.builder()
            .id(id)
            .level(level)
            .levelCompetences(levelCompetences().stream()
                .map(LevelCompetenceDto::dtoToDomain)
                .toList())
            .build();
    }
}
