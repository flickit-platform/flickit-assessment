package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.util.NumberUtils.isLessThanWithPrecision;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AttributeValue {

    private final UUID id;
    private final Attribute attribute;
    private final List<Answer> answers;
    private Set<MaturityScore> maturityScores = new HashSet<>();
    private MaturityLevel maturityLevel;
    private Double confidenceValue;

    public void calculate(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> totalScore = calcTotalScore(maturityLevels);
        Map<Long, Double> gainedScore = calcGainedScore(maturityLevels);
        Map<Long, Double> percentScore = calcPercent(totalScore, gainedScore);
        maturityLevels.forEach(ml -> {
            long maturityLevelId = ml.getId();
            MaturityScore maturityScore = new MaturityScore(maturityLevelId, percentScore.get(maturityLevelId));
            maturityScores.add(maturityScore);
        });
        maturityLevel = findGainedMaturityLevel(percentScore, maturityLevels);
    }

    private Map<Long, Double> calcTotalScore(List<MaturityLevel> maturityLevels) {
        if (attribute.getQuestions() == null)
            return new HashMap<>();
        return maturityLevels.stream()
            .flatMap(ml ->
                attribute.getQuestions().stream()
                    .filter(question -> !isMarkedAsNotApplicable(question.getId()))
                    .map(question -> question.findImpactByAttributeAndMaturityLevel(this.getAttribute().getId(), ml.getId()))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(ml, impact.getWeight()))
            ).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
    }

    private boolean isMarkedAsNotApplicable(Long questionId) {
        return answers.stream()
            .anyMatch(answer -> answer.getQuestionId().equals(questionId) && Boolean.TRUE.equals(answer.getIsNotApplicable()));
    }

    private Map<Long, Double> calcGainedScore(List<MaturityLevel> maturityLevels) {
        return maturityLevels.stream()
            .flatMap(ml -> {
                assert attribute.getQuestions() != null;
                Map<Long, QuestionImpact> questionIdToQuestionImpact = new HashMap<>();
                for (Question q : attribute.getQuestions()) {
                    var impact = q.findImpactByAttributeAndMaturityLevel(this.getAttribute().getId(), ml.getId());
                    if (impact != null) // Only add non-null impacts to the map
                        questionIdToQuestionImpact.put(q.getId(), impact);
                }
                if (questionIdToQuestionImpact.isEmpty())
                    return Stream.of(new MaturityLevelScore(ml, 0.0));

                return answers.stream()
                    .filter(answer -> !Boolean.TRUE.equals(answer.getIsNotApplicable()) && answer.getSelectedOption() != null)
                    .map(answer -> {
                        var score = 0.0;
                        var impact = questionIdToQuestionImpact.get(answer.getQuestionId());
                        if (impact != null)
                            score = answer.getSelectedOption().getValue() * impact.getWeight();
                        return new MaturityLevelScore(ml, score);
                    });
            }).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
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
            .sorted(comparingInt(MaturityLevel::getIndex))
            .toList();

        MaturityLevel result = null;
        for (MaturityLevel ml : sortedMaturityLevels) {
            if (!passLevel(percentScore, ml))
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

    private record MaturityLevelScore(MaturityLevel maturityLevel, double score) {
    }

    public int getWeightedLevel() {
        Assert.notNull(maturityLevel, () -> "maturityLevel should not be null");
        return maturityLevel.getValue() * attribute.getWeight();
    }

    public Map<Long, Double> getWeightedScore() {
        Map<Long, Double> weightedScores = new HashMap<>();

        for (MaturityScore maturityScore : maturityScores) {
            Long maturityLevelId = maturityScore.getMaturityLevelId();
            double score = maturityScore.getScore() == null ? 0 : maturityScore.getScore(); //todo:redundant nullability check
            double weightedScore = score * (attribute.getWeight());
            weightedScores.put(maturityLevelId, weightedScore);
        }

        return weightedScores;
    }

    public void calculateConfidenceValue() {
        var questionIdToWeightMap = computeQuestionsWeight();
        if (questionIdToWeightMap == null || questionIdToWeightMap.isEmpty()) {
            this.confidenceValue = null;
            return;
        }
        Double totalScore = calcConfidenceTotalScore(questionIdToWeightMap);
        Double gainedScore = calcConfidenceGainedScore(questionIdToWeightMap);
        this.confidenceValue = (gainedScore / totalScore) * 100;
    }

    private Map<Long, Double> computeQuestionsWeight() {
        if (answers == null || attribute.getQuestions() == null) {
            return Collections.emptyMap();
        }
        List<Answer> notApplicableAnswers = answers.stream()
            .filter(x -> Boolean.TRUE.equals(x.getIsNotApplicable()))
            .toList();
        return attribute.getQuestions().stream()
            .filter(q -> notApplicableAnswers.stream().noneMatch(a -> a.getQuestionId().equals(q.getId())))
            .collect(Collectors.toMap(Question::getId, AttributeValue::calculateQuestionWeight));
    }

    private static double calculateQuestionWeight(Question question) {
        Assert.notNull(question.getImpacts(), () -> "Question impacts must not be null.");
        Assert.notEmpty(question.getImpacts(), () -> "Question impacts must not be empty.");
        double sum = question.getImpacts().stream()
            .mapToDouble(QuestionImpact::getWeight)
            .sum();
        return sum / question.getImpacts().size();
    }

    private Double calcConfidenceTotalScore(Map<Long, Double> questionIdToWeightMap) {
        return questionIdToWeightMap.keySet().stream()
            .mapToDouble(question -> {
                Double questionWeight = questionIdToWeightMap.get(question);
                return questionWeight * ConfidenceLevel.getMaxLevel().getId();
            })
            .sum();
    }

    private double calcConfidenceGainedScore(Map<Long, Double> questionIdToWeightMap) {
        Map<Long, Integer> questionIdToAnswerConfidenceLevelId = new HashMap<>();
        answers.forEach(e -> {
            if (!Boolean.TRUE.equals(e.getIsNotApplicable()))
                questionIdToAnswerConfidenceLevelId.put(e.getQuestionId(), e.getConfidenceLevelId());
        });

        return questionIdToWeightMap.keySet().stream()
            .mapToDouble(questionId -> {
                Double questionWeight = questionIdToWeightMap.get(questionId);
                Integer confidenceLevelId = questionIdToAnswerConfidenceLevelId.get(questionId);
                if (confidenceLevelId == null)
                    return 0;
                else
                    return questionWeight * confidenceLevelId;
            })
            .sum();
    }

    public Double getWeightedConfidenceValue() {
        return confidenceValue * attribute.getWeight();
    }
}
