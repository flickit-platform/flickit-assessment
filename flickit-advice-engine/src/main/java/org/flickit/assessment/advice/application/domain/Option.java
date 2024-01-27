package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Option {

    private long id;
    private int index;
    private Map<AttributeLevelScore, Double> promisedScores;
    private double progress;
    private int questionCost;

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
