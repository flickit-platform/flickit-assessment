package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence.LevelCompetenceDto;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.List;

public record MaturityLevelDto(Long id,
                               @JsonProperty("value")
                               Integer level,
                               @JsonProperty("level_competences")
                               List<LevelCompetenceDto> levelCompetences) {

    public MaturityLevel dtoToDomain() {
        List<LevelCompetence> competences = levelCompetences().stream()
            .map(LevelCompetenceDto::dtoToDomain)
            .toList();
        return new MaturityLevel(id, level, competences);
    }
}
