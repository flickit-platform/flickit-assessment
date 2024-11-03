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
    private final long kitVersionId;

    @Setter
    private List<SubjectValue> subjectValues;

    @Setter
    private MaturityLevel maturityLevel;

    @Setter
    private Double confidenceValue;

    @Setter
    private Boolean isCalculateValid;

    @Setter
    private Boolean isConfidenceValid;

    @Setter
    private LocalDateTime lastModificationTime;

    @Setter
    private LocalDateTime lastCalculationTime;

    @Setter
    private LocalDateTime lastConfidenceCalculationTime;

    public AssessmentResult(UUID assessmentResultId, Assessment assessment, long kitVersionId, List<SubjectValue> subjectValues,
                            LocalDateTime lastCalculationTime, LocalDateTime lastConfidenceCalculationTime) {
        this.id = assessmentResultId;
        this.assessment = assessment;
        this.kitVersionId = kitVersionId;
        this.subjectValues = subjectValues;
        this.lastCalculationTime = lastCalculationTime;
        this.lastConfidenceCalculationTime = lastConfidenceCalculationTime;
    }


    public MaturityLevel calculate() {
        List<MaturityLevel> maturityLevels = assessment.getAssessmentKit().getMaturityLevels();
        calculateSubjectValuesAndSetMaturityLevel(maturityLevels);
        int weightedMeanLevel = calculateWeightedMeanOfSubjectValues();
        return maturityLevels.stream()
            .filter(m -> m.getValue() == weightedMeanLevel)
            .findAny()
            .orElseThrow(IllegalStateException::new);
    }

    private void calculateSubjectValuesAndSetMaturityLevel(List<MaturityLevel> maturityLevels) {
        subjectValues.forEach(x -> {
            MaturityLevel calcResult = x.calculate(maturityLevels);
            x.setMaturityLevel(calcResult);
        });
    }

    private int calculateWeightedMeanOfSubjectValues() {
        MutableInt weightedSum = new MutableInt();
        MutableInt sum = new MutableInt();
        subjectValues.forEach(x -> {
                weightedSum.add(x.getWeightedLevel());
                sum.add(x.getSubject().getWeight());
            });
        return (int) Math.round((double) weightedSum.getValue() / sum.getValue());
    }

    public Double calculateConfidenceValue() {
        calculateSubjectValuesAndSetConfidenceValue();
        return calculateWeightedMeanOfAttributeConfidenceValues();
    }

    private void calculateSubjectValuesAndSetConfidenceValue() {
        subjectValues.forEach(x -> {
            Double calcResult = x.calculateConfidenceValue();
            x.setConfidenceValue(calcResult);
        });
    }

    private Double calculateWeightedMeanOfAttributeConfidenceValues() {
        MutableDouble weightedSum = new MutableDouble();
        MutableDouble sum = new MutableDouble();
        subjectValues.stream()
            .flatMap(x -> x.getAttributeValues().stream())
            .filter(x -> x.getConfidenceValue() != null)
            .forEach(x -> {
                weightedSum.add(x.getWeightedConfidenceValue());
                sum.add(x.getAttribute().getWeight());
            });
        return sum.getValue() == 0 ? null : weightedSum.getValue() / sum.getValue();
    }

}
