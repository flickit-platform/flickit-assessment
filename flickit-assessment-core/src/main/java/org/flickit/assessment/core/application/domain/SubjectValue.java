package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class SubjectValue {

    private final UUID id;
    private final Subject subject;

    @Setter
    private List<QualityAttributeValue> qualityAttributeValues;

    @Setter
    MaturityLevel maturityLevel;

    @Setter
    Double confidenceValue;

    public SubjectValue(UUID id, Subject subject, List<QualityAttributeValue> qavList) {
        this.id = id;
        this.subject = subject;
        this.qualityAttributeValues = qavList;
    }

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
        qualityAttributeValues.forEach(x -> x.calculate(maturityLevels));

        int weightedMeanLevel = calculateWeightedMeanOfAttributeValues();
        return maturityLevels.stream()
            .filter(m -> m.getValue() == weightedMeanLevel)
            .findAny()
            .orElseThrow(IllegalStateException::new);
    }

    private int calculateWeightedMeanOfAttributeValues() {
        int weightedSum = 0;
        int sum = 0;
        for (QualityAttributeValue qav : qualityAttributeValues) {
            weightedSum += qav.getWeightedLevel();
            sum += qav.getQualityAttribute().getWeight();
        }
        return sum != 0 ? (int) Math.round((double) weightedSum / sum) : 0;
    }

    public Double calculateConfidenceValue() {
        qualityAttributeValues.forEach(QualityAttributeValue::calculateConfidenceValue);
        return calculateWeightedMeanOfAttributeConfidenceValues();
    }

    private Double calculateWeightedMeanOfAttributeConfidenceValues() {
        MutableDouble weightedSum = new MutableDouble();
        MutableDouble sum = new MutableDouble();
        for (QualityAttributeValue qav : qualityAttributeValues) {
            if (qav.getConfidenceValue() != null) {
                weightedSum.add(qav.getWeightedConfidenceValue());
                sum.add(qav.getQualityAttribute().getWeight());
            }
        }
        return sum.getValue() == 0 ? null : weightedSum.getValue() / sum.getValue();
    }

}
