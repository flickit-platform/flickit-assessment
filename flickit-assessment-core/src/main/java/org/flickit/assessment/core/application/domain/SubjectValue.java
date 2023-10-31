package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class SubjectValue {

    private final UUID id;
    private final Subject subject;
    private final List<QualityAttributeValue> qualityAttributeValues;

    @Setter
    MaturityLevel maturityLevel;

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
        qualityAttributeValues.forEach(x -> x.calculate(maturityLevels));

        int weightedMeanLevel = calculateWeightedMeanOfQualityAttributeValues();
        return maturityLevels.stream()
            .filter(m -> m.getLevel() == weightedMeanLevel)
            .findAny()
            .orElseThrow(IllegalStateException::new);
    }

    private int calculateWeightedMeanOfQualityAttributeValues() {
        int weightedSum = 0;
        int sum = 0;
        for (QualityAttributeValue qav : qualityAttributeValues) {
            weightedSum += qav.getWeightedLevel();
            sum += qav.getQualityAttribute().getWeight();
        }
        return (int) Math.round((double) weightedSum / sum);
    }

}
