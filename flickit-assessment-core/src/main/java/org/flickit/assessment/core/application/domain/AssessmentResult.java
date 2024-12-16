package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

import java.time.LocalDateTime;
import java.util.*;

import static org.flickit.assessment.common.util.NumberUtils.isLessThanWithPrecision;

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
        var maturityLevelIdToScores = calculateSubjectWeightedMeanScoresByMaturityLevel(maturityLevels);

        return findGainedMaturityLevel(maturityLevelIdToScores, maturityLevels);
    }

    private void calculateSubjectValuesAndSetMaturityLevel(List<MaturityLevel> maturityLevels) {
        subjectValues.forEach(e -> {
            MaturityLevel calcResult = e.calculate(maturityLevels);
            e.setMaturityLevel(calcResult);
        });
    }

    private Map<Long, Double> calculateSubjectWeightedMeanScoresByMaturityLevel(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> levelIdToSubjectsWeightedScoreSum = new HashMap<>();
        MutableInt subjectsTotalWeight = new MutableInt();

        subjectValues.forEach(e -> {
            Map<Long, Double> maturityLevelWeightedScore = e.getSubjectLevelWeightedScore();
            int weight = e.getSubject().getWeight();
            subjectsTotalWeight.add(weight);
            maturityLevels.forEach(x -> {
                long mLevelId = x.getId();
                double weightedScore = maturityLevelWeightedScore.get(mLevelId);
                levelIdToSubjectsWeightedScoreSum.merge(mLevelId, weightedScore, Double::sum);
            });
        });

        Map<Long, Double> mLevelIdToScoreWeightedMean = new HashMap<>();
        levelIdToSubjectsWeightedScoreSum.forEach((mLevelId, weightedScoreSum) -> {
            if (subjectsTotalWeight.intValue() > 0)
                mLevelIdToScoreWeightedMean.put(mLevelId, weightedScoreSum/ subjectsTotalWeight.intValue());
            else
                mLevelIdToScoreWeightedMean.put(mLevelId, 0d);
        });
        return mLevelIdToScoreWeightedMean;
    }

    private MaturityLevel findGainedMaturityLevel(Map<Long, Double> maturityLevelIdToScores, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(Comparator.comparingInt(MaturityLevel::getIndex))
            .toList();

        MaturityLevel result = null;
        for (MaturityLevel ml : sortedMaturityLevels) {
            if (!passLevel(maturityLevelIdToScores, ml))
                break;

            result = ml;
        }
        return result;
    }

    private boolean passLevel(Map<Long, Double> percentScores, MaturityLevel ml) {
        List<LevelCompetence> levelCompetences = ml.getLevelCompetences();

        for (LevelCompetence levelCompetence : levelCompetences) {
            Long mlId = levelCompetence.getEffectiveLevelId();
            if (percentScores.containsKey(mlId) && isLessThanWithPrecision(percentScores.get(mlId), levelCompetence.getValue()))
                return false;
        }
        return true;
    }

    public Double calculateConfidenceValue() {
        calculateSubjectValuesAndSetConfidenceValue();
        return calculateWeightedMeanOfSubjectConfidenceValues();
    }

    private void calculateSubjectValuesAndSetConfidenceValue() {
        subjectValues.forEach(x -> {
            Double calcResult = x.calculateConfidenceValue();
            x.setConfidenceValue(calcResult);
        });
    }

    private Double calculateWeightedMeanOfSubjectConfidenceValues() {
        MutableDouble weightedSum = new MutableDouble();
        MutableDouble sum = new MutableDouble();
        subjectValues.stream()
            .filter(x -> x.getConfidenceValue() != null)
            .forEach(x -> {
                weightedSum.add(x.getWeightedConfidenceValue());
                sum.add(x.getSubject().getWeight());
            });
        return sum.getValue() == 0 ? 0 : weightedSum.getValue() / sum.getValue();
    }
}
