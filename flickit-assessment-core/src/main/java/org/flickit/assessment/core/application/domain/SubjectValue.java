package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.*;

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

    Map<Long, Double> mLevelIdToAttrScoreWeightedMean = new HashMap<>();

    public SubjectValue(UUID id, Subject subject, List<AttributeValue> qavList) {
        this.id = id;
        this.subject = subject;
        this.attributeValues = qavList;
    }

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
        attributeValues.forEach(attributeValue -> attributeValue.calculate(maturityLevels));
        mLevelIdToAttrScoreWeightedMean = calculateAttrLevelScoreWeightedMean(maturityLevels);

        return findGainedMaturityLevel(mLevelIdToAttrScoreWeightedMean, maturityLevels);
    }

    public Map<Long, Double> getSubjectLevelWeightedScore() {
        Map<Long, Double> levelIdToWeightedScore = new HashMap<>();
        mLevelIdToAttrScoreWeightedMean.forEach((mLevelId, attributeScoreWeightedMean) -> {
            levelIdToWeightedScore.put(mLevelId, attributeScoreWeightedMean * subject.getWeight());
        });

        return levelIdToWeightedScore;
    }

    private Map<Long, Double> calculateAttrLevelScoreWeightedMean(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> weightedSum = new HashMap<>();
        int totalWeight = 0;

        for (AttributeValue attributeValue : attributeValues) {
            Map<Long, Double> attributeWeightedScores = attributeValue.getWeightedScore();
            int attributeWeight = attributeValue.getAttribute().getWeight();
            totalWeight += attributeWeight;

            for (MaturityLevel ml : maturityLevels) {
                Long maturityLevelId = ml.getId();
                Double weightedScore = attributeWeightedScores.get(maturityLevelId);

                if (weightedScore != null) //todo: redundant nullability check
                    weightedSum.merge(maturityLevelId, weightedScore, Double::sum);
            }
        }

        for (Map.Entry<Long, Double> entry : weightedSum.entrySet()) {
            Long maturityLevelId = entry.getKey();
            Double sumScores = entry.getValue();

            if (totalWeight > 0)
                mLevelIdToAttrScoreWeightedMean.put(maturityLevelId, sumScores / totalWeight);
            else
                mLevelIdToAttrScoreWeightedMean.put(maturityLevelId, 0.0);
        }

        return mLevelIdToAttrScoreWeightedMean;
    }

    private MaturityLevel findGainedMaturityLevel(Map<Long, Double> percentScores, List<MaturityLevel> maturityLevels) {
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
