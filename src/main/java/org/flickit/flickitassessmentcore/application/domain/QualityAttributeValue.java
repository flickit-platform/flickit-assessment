package org.flickit.flickitassessmentcore.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class QualityAttributeValue {

    private final UUID id;
    private final QualityAttribute qualityAttribute;
    private final List<Answer> answers;

    @Setter
    MaturityLevel maturityLevel;

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> totalScore = calcTotalScore(maturityLevels);
        Map<Long, Double> gainedScore = calcGainedScore(maturityLevels);
        Map<Long, Double> percentScore = calcPercent(totalScore, gainedScore);
        return findGainedMaturityLevel(percentScore, maturityLevels);
    }

    private Map<Long, Double> calcTotalScore(List<MaturityLevel> maturityLevels) {
        if (qualityAttribute.getQuestions() == null)
            return new HashMap<>();
        return maturityLevels.stream()
            .flatMap(ml ->
                qualityAttribute.getQuestions().stream()
                    .map(question -> question.findImpactByMaturityLevel(ml))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(ml, impact.getWeight()))
            ).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
    }

    private Map<Long, Double> calcGainedScore(List<MaturityLevel> maturityLevels) {
        return maturityLevels.stream()
            .flatMap(ml ->
                answers.stream()
                    .map(answer -> answer.findImpactByMaturityLevel(ml))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(ml, impact.calculateScore()))
            ).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
    }

    private Map<Long, Double> calcPercent(Map<Long, Double> totalScore, Map<Long, Double> gainedScore) {
        return totalScore.entrySet().stream()
            .map(e -> {
                Double gained = gainedScore.get(e.getKey());
                double percent = gained != null ? (gained / e.getValue()) * 100 : 0;
                return Map.entry(e.getKey(), percent);
            }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private MaturityLevel findGainedMaturityLevel(Map<Long, Double> percentScore, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getLevel))
            .toList();

        MaturityLevel maxPossibleMaturityLevel = null;
        for (MaturityLevel ml : sortedMaturityLevels)
            maxPossibleMaturityLevel = percentScore.containsKey(ml.getId()) ? ml : maxPossibleMaturityLevel;

        MaturityLevel result = null;
        for (MaturityLevel ml : sortedMaturityLevels) {
            List<LevelCompetence> levelCompetences = ml.getLevelCompetences();
            if (levelCompetences.isEmpty()) {
                result = ml;
                continue;
            }
            boolean allCompetencesMatched = levelCompetences.stream()
                .allMatch(levelCompetence -> {
                    Long mlId = levelCompetence.getMaturityLevelId();
                    return !percentScore.containsKey(mlId) || percentScore.get(mlId) >= levelCompetence.getValue();
                });

            if (allCompetencesMatched && maxPossibleMaturityLevel != null && maxPossibleMaturityLevel.getLevel() >= ml.getLevel())
                result = ml;
            else
                break;
        }
        return result;
    }

    private record MaturityLevelScore(MaturityLevel maturityLevel, double score) {
    }

    public int getWeightedLevel() {
        Assert.notNull(maturityLevel, () -> "maturityLevel should not be null");
        return maturityLevel.getLevel() * qualityAttribute.getWeight();
    }
}
