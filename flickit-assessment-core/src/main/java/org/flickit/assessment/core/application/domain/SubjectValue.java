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

    public SubjectValue(UUID id, Subject subject, List<AttributeValue> qavList) {
        this.id = id;
        this.subject = subject;
        this.attributeValues = qavList;
    }

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
            attributeValues.forEach(attributeValue -> attributeValue.calculate(maturityLevels));
            Map<Long, Double> weightedMeanScores = calculateWeightedMeanScoresOfAttributeValues(maturityLevels);

            return findGainedMaturityLevel(weightedMeanScores, maturityLevels);
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

    private Map<Long, Double> calculateWeightedMeanScoresOfAttributeValues(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> weightedSum = new HashMap<>();
        Map<Long, Double> totalWeight = new HashMap<>();

        for (AttributeValue attributeValue : attributeValues) {
            Map<Long, Double> attributeWeightedScores = attributeValue.getWeightedScore();

            for (MaturityLevel ml : maturityLevels) {
                Long maturityLevelId = ml.getId();
                Double weightedScore = attributeWeightedScores.get(maturityLevelId);

                if (weightedScore != null) {
                    weightedSum.merge(maturityLevelId, weightedScore, Double::sum);
                    totalWeight.merge(maturityLevelId, (double) attributeValue.getAttribute().getWeight(), Double::sum);
                }
            }
        }

        Map<Long, Double> weightedMeanScores = new HashMap<>();
        for (Map.Entry<Long, Double> entry : weightedSum.entrySet()) {
            Long maturityLevelId = entry.getKey();
            Double sumScores = entry.getValue();
            Double sumWeights = totalWeight.get(maturityLevelId);

            if (sumWeights != null && sumWeights > 0)
                weightedMeanScores.put(maturityLevelId, sumScores / sumWeights);

            else
                weightedMeanScores.put(maturityLevelId, 0.0);
        }

        return weightedMeanScores;
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
            if (percentScores.containsKey(mlId) && percentScores.get(mlId) < levelCompetence.getValue()) {
                return false;
            }
        }
        return true;
    }
}
