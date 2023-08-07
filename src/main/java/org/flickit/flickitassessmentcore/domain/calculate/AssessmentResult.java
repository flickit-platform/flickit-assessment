package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AssessmentResult {

    UUID id;
    List<SubjectValue> subjectValues;
    Assessment assessment;

    @Setter
    MaturityLevel maturityLevel;

    @Setter
    boolean isValid;

    public AssessmentResult(UUID id, List<SubjectValue> subjectValues, Assessment assessment) {
        this.id = id;
        this.subjectValues = subjectValues;
        this.assessment = assessment;
    }

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

}
