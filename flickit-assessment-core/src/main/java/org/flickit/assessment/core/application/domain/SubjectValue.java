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
    private List<AttributeValue> attributeValues;

    @Setter
    MaturityLevel maturityLevel;

    @Setter
    Double confidenceValue;

    public SubjectValue(UUID id, Subject subject, List<AttributeValue> qavList) {
        this.id = id;
        this.subject = subject;
        this.attributeValues = qavList;
    }

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
        attributeValues.forEach(x -> x.calculate(maturityLevels));

        int weightedMeanLevel = calculateWeightedMeanOfAttributeValues();
        return maturityLevels.stream()
            .filter(m -> m.getValue() == weightedMeanLevel)
            .findAny()
            .orElseThrow(IllegalStateException::new);
    }

    private int calculateWeightedMeanOfAttributeValues() {
        int weightedSum = 0;
        int sum = 0;
        for (AttributeValue qav : attributeValues) {
            weightedSum += qav.getWeightedLevel();
            sum += qav.getAttribute().getWeight();
        }
        return sum != 0 ? (int) Math.round((double) weightedSum / sum) : 0;
    }

    public Double calculateConfidenceValue() {
        attributeValues.forEach(AttributeValue::calculateConfidenceValue);
        return calculateWeightedMeanOfAttributeConfidenceValues();
    }

    private Double calculateWeightedMeanOfAttributeConfidenceValues() {
        MutableDouble weightedSum = new MutableDouble();
        MutableDouble sum = new MutableDouble();
        for (AttributeValue qav : attributeValues) {
            if (qav.getConfidenceValue() != null) {
                weightedSum.add(qav.getWeightedConfidenceValue());
                sum.add(qav.getAttribute().getWeight());
            }
        }
        return sum.getValue() == 0 ? null : weightedSum.getValue() / sum.getValue();
    }

}
