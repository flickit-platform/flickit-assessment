package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttributeLevelScore {

    private double gainedScore;
    private double requiredScore;
    private long attributeId;
    private long maturityLevelId;

    public double getRemainingScore() {
        return this.requiredScore - this.gainedScore;
    }
}
