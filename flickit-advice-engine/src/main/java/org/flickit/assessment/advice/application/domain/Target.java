package org.flickit.assessment.advice.application.domain;

import lombok.Getter;

@Getter
public final class Target {

    private final double currentGain;
    private final double minGain;
    private final double neededGain;

    public Target(double currentGain, double minGain) {
        this.currentGain = currentGain;
        this.minGain = minGain;
        this.neededGain = minGain - currentGain;
    }

    @Override
    public String toString() {
        return "Target[" +
            "currentGain=" + currentGain + ", " +
            "minGain=" + minGain + ", " +
            "neededGain=" + neededGain + ']';
    }
}
