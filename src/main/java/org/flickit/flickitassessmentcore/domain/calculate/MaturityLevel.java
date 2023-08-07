package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class MaturityLevel {

    long id;
    int level;
    List<LevelCompetence> levelCompetences;
}
