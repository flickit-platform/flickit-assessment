package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttributeLevelScore {

    private final double gainedScore;
    private final double requiredScore;
    private final long attributeId;
    private final long maturityLevelId;

    public double getRemainingScore() {
        return this.requiredScore - this.gainedScore;
    }
}
