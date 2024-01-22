package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    private Map<Target, Double> gains;
    private double progress;
    private int questionCost;

    public double getTargetGain(Target target) {
        return gains.get(target);
    }

    public double getCost() {
        return progress * questionCost;
    }

    public boolean hasImpact(Target target) {
        return gains.containsKey(target);
    }

    public double getAllGains() {
        return gains.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
}
