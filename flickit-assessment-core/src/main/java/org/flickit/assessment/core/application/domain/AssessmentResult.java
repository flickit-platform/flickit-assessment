package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

import java.time.LocalDateTime;
import java.util.*;

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
        var weightedMeanLevel = calculateWeightedMeanOfSubjectValues(maturityLevels);

        return findGainedMaturitryLevel(weightedMeanLevel, maturityLevels);
    }

    private Map<Long, Double> calculateWeightedMeanOfSubjectValues(List<MaturityLevel> maturityLevels) {
        subjectValues.forEach(x -> {
            MaturityLevel calcResult = x.calculate(maturityLevels);
            x.setMaturityLevel(calcResult);
        });

        Map<Long, Double> weightedSum = new HashMap<>();
        MutableInt weightsSum = new MutableInt();

        subjectValues.forEach(e -> {
            Map<Long, Double> maturityLevelWeightedScore = e.getWeightedScore();
            int weight = e.getSubject().getWeight();
            weightsSum.add(weight);
            maturityLevels.forEach(x -> {
                long mLevelId = x.getId();
                double weightedScore = maturityLevelWeightedScore.get(mLevelId);
                weightedSum.merge(mLevelId, weightedScore, Double::sum);
            });
        });

        Map<Long, Double> weightedMeanScores = new HashMap<>();
        weightedSum.forEach((mLevelId, scoresWeightedSum) -> {
            if (weightsSum.intValue() > 0)
                weightedMeanScores.put(mLevelId, scoresWeightedSum/ weightsSum.intValue());
            else
                weightedMeanScores.put(mLevelId, 0d);
        });
        return weightedMeanScores;
    }

    private MaturityLevel findGainedMaturitryLevel(Map<Long, Double> percentScores, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(Comparator.comparingInt(MaturityLevel::getIndex))
            .toList();

        MaturityLevel result = null;
        for (MaturityLevel ml : sortedMaturityLevels) {
            if (!passLevel(percentScores, ml))
                break;

            result = ml;
        }
        return result;
    }

    private boolean passLevel(Map<Long, Double> percentScores, MaturityLevel ml) {
        List<LevelCompetence> levelCompetences = ml.getLevelCompetences();

        for (LevelCompetence levelCompetence : levelCompetences) {
            Long mlId = levelCompetence.getEffectiveLevelId();
            if (percentScores.containsKey(mlId) && percentScores.get(mlId) < levelCompetence.getValue())
                return false;
        }
        return true;
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
