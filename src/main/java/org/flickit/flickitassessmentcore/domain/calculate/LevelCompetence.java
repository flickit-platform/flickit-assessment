package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class LevelCompetence {

    Long id;
    int value;
    long maturityLevelId;
}
