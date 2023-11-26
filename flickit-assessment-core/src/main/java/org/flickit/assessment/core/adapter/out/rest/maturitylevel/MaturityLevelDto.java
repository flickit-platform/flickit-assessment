package org.flickit.assessment.core.adapter.out.rest.maturitylevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.adapter.out.rest.levelcompetence.LevelCompetenceDto;
import org.flickit.assessment.core.application.domain.LevelCompetence;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;

public record MaturityLevelDto(Long id,
                               @JsonProperty("index")
                               Integer index,
                               @JsonProperty("value")
                               Integer value,
                               @JsonProperty("level_competences")
                               List<LevelCompetenceDto> levelCompetences) {

    public MaturityLevel dtoToDomain() {
        List<LevelCompetence> competences = levelCompetences().stream()
            .map(LevelCompetenceDto::dtoToDomain)
            .toList();
        return new MaturityLevel(id, index, value, competences);
    }
}
