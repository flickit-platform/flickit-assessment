package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Option {

    private final long id;
    private final int index;
    private final Map<AttributeLevelScore, Double> promisedScores;
    private final double progress;
    private final int questionCost;

    public double getAttributeLevelPromisedScore(AttributeLevelScore attributeLevelScore) {
        return promisedScores.get(attributeLevelScore);
    }

    public double getCost() {
        return progress * questionCost;
    }

    public boolean hasImpact(AttributeLevelScore attributeLevelScore) {
        return promisedScores.containsKey(attributeLevelScore);
    }

    public double sumScores() {
        return promisedScores.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
}
