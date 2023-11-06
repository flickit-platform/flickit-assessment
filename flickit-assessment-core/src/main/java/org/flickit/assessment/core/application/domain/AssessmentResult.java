package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AssessmentResult {

    private final UUID id;
    private final Assessment assessment;
    private final List<SubjectValue> subjectValues;

    @Setter
    MaturityLevel maturityLevel;

    @Setter
    double confidenceLevelValue;

    @Setter
    boolean isCalculateValid;

    @Setter
    boolean isConfidenceValid;

    @Setter
    LocalDateTime lastModificationTime;


    public MaturityLevel calculate() {
        List<MaturityLevel> maturityLevels = assessment.getAssessmentKit().getMaturityLevels();
        calculateSubjectValuesAndSetMaturityLevel(maturityLevels);
        int weightedMeanLevel = calculateWeightedMeanOfQualityAttributeValues();
        return maturityLevels.stream()
            .filter(m -> m.getLevel() == weightedMeanLevel)
            .findAny()
            .orElseThrow(IllegalStateException::new);
    }

    private void calculateSubjectValuesAndSetMaturityLevel(List<MaturityLevel> maturityLevels) {
        subjectValues.forEach(x -> {
            MaturityLevel calcResult = x.calculate(maturityLevels);
            x.setMaturityLevel(calcResult);
        });
    }

    private int calculateWeightedMeanOfQualityAttributeValues() {
        MutableInt weightedSum = new MutableInt();
        MutableInt sum = new MutableInt();
        subjectValues.stream()
            .flatMap(x -> x.getQualityAttributeValues().stream())
            .forEach(x -> {
                weightedSum.add(x.getWeightedLevel());
                sum.add(x.getQualityAttribute().getWeight());
            });
        return (int) Math.round((double) weightedSum.getValue() / sum.getValue());
    }

    public double calculateConfidenceLevel() {
        calculateSubjectValuesAndSetConfidenceLevelValue();
        double weightedMeanLevel = calculateWeightedMeanOfQualityAttributeConfidenceLevelValues();
        return weightedMeanLevel;
    }

    private void calculateSubjectValuesAndSetConfidenceLevelValue() {
        subjectValues.forEach(x -> {
            double calcResult = x.calculate();
            x.setConfidenceLevelValue(calcResult);
        });
    }

    private double calculateWeightedMeanOfQualityAttributeConfidenceLevelValues() {
        MutableDouble weightedSum = new MutableDouble();
        MutableDouble sum = new MutableDouble();
        subjectValues.stream()
            .flatMap(x -> x.getQualityAttributeValues().stream())
            .forEach(x -> {
                weightedSum.add(x.getWeightedConfidenceLevel());
                sum.add(x.getQualityAttribute().getWeight());
            });
        return weightedSum.getValue() / sum.getValue();
    }

}
