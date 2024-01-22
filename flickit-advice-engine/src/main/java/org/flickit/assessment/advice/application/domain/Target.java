package org.flickit.assessment.advice.application.domain;

import lombok.Getter;

@Getter
public final class Target {

    private final int currentGain;
    private final int minGain;
    private final int neededGain;

    public Target(int currentGain, int minGain) {
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
