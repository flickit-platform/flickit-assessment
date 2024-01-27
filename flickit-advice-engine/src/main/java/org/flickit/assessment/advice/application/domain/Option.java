package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Option {

    private long id;
    private int index;
    private Map<AttributeLevelScore, Double> gains;
    private double progress;
    private int questionCost;

    public double getTargetGain(AttributeLevelScore attributeLevelScore) {
        return gains.get(attributeLevelScore);
    }

    public double getCost() {
        return progress * questionCost;
    }

    public boolean hasImpact(AttributeLevelScore attributeLevelScore) {
        return gains.containsKey(attributeLevelScore);
    }

    public double sumScores() {
        return gains.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
}
