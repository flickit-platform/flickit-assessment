package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

@Getter
@Builder(toBuilder = true)
public class QualityAttributeValue {

    UUID id;
    List<Answer> answers;
    QualityAttribute qualityAttribute;

    @Setter
    MaturityLevel maturityLevel;

    public MaturityLevel calculate(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> totalScore = calcTotalScore(maturityLevels);
        Map<Long, Double> gainedScore = calcGainedScore(maturityLevels);
        Map<Long, Double> percentScore = calcPercent(totalScore, gainedScore);
        return findGainedMaturityLevel(percentScore, maturityLevels);
    }

    private Map<Long, Double> calcTotalScore(List<MaturityLevel> maturityLevels) {
        return maturityLevels.stream()
            .flatMap(maturityLevel ->
                qualityAttribute.getQuestions().stream()
                    .map(question -> question.findImpactByMaturityLevel(maturityLevel))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(maturityLevel, impact.getWeight()))
            ).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
    }

    private Map<Long, Double> calcGainedScore(List<MaturityLevel> maturityLevels) {
        return maturityLevels.stream()
            .flatMap(maturityLevel ->
                answers.stream()
                    .map(answer -> answer.findImpactByMaturityLevel(maturityLevel))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(maturityLevel, impact.calculateScore()))
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
        for (MaturityLevel maturityLevel : sortedMaturityLevels)
            maxPossibleMaturityLevel = percentScore.containsKey(maturityLevel.getId()) ?
                maturityLevel :
                maxPossibleMaturityLevel;

        MaturityLevel result = null;
        for (MaturityLevel maturityLevel : sortedMaturityLevels) {
            List<LevelCompetence> levelCompetences = maturityLevel.getLevelCompetences();
            if (levelCompetences.isEmpty()) {
                result = maturityLevel;
                continue;
            }
            boolean allCompetencesMatched = levelCompetences.stream()
                .allMatch(levelCompetence -> {
                    Long id = levelCompetence.getMaturityLevelId();
                    return !percentScore.containsKey(id) || percentScore.get(id) >= levelCompetence.getValue();
                });

            if (allCompetencesMatched && maxPossibleMaturityLevel != null && maxPossibleMaturityLevel.getLevel() >= maturityLevel.getLevel())
                result = maturityLevel;
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
