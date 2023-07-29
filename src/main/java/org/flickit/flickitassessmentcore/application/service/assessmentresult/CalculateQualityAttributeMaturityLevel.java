package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class CalculateQualityAttributeMaturityLevel {

    private final LoadQuestionsByQualityAttributePort loadQuestionsByQualityAttributePort;
    private final LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
    private final LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
    private final LoadQuestionImpactPort loadQuestionImpactPort;

    public MaturityLevel calculate(UUID assessmentResultId, List<MaturityLevel> maturityLevels, Long qualityAttributeId) {
        List<Question> questions = loadQuestionsByQualityAttributePort.loadByQualityAttributeId(new LoadQuestionsByQualityAttributePort.Param(qualityAttributeId)).questions();

        Map<Long, Double> maturityLevelMaxScoreMap = calculateMaturityLevelsMaxScore(questions, qualityAttributeId);

        Map<Long, Double> maturityLevelEarnedScoreMap = new HashMap<>();

        for (Question question : questions) {
            Long answerOptionId = findQuestionAnswer(assessmentResultId, question.getId());
            if (answerOptionId == null)
                continue;

            List<AnswerOptionImpact> answerOptionImpacts = loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort
                .loadByAnswerOptionIdAndQualityAttributeId(answerOptionId, qualityAttributeId).optionImpacts();

            for (AnswerOptionImpact impact : answerOptionImpacts) {
                Long questionImpactId = impact.getQuestionImpactId();
                QuestionImpact questionImpact = loadQuestionImpactPort.load(questionImpactId).questionImpact();
                Long maturityLevelId = questionImpact.getMaturityLevelId();

                Double value = impact.getValue() * questionImpact.getWeight();
                log.debug("Question: [{}] with Option: [{}] as answer, has value: [{}], on ml: [{}]",
                    question.getTitle(), answerOptionId, value, maturityLevelId);
                maturityLevelEarnedScoreMap.put(maturityLevelId, maturityLevelEarnedScoreMap.getOrDefault(maturityLevelId, 0.0) + value);
            }
        }
        Map<Long, Double> maturityLevelsScoreMap = calculatePercentageScoreMap(maturityLevelEarnedScoreMap, maturityLevelMaxScoreMap);
        // We have to create a new list, because the output of called method is immutable list, so we can't do anything on it further.
        return findAchievedMaturityLevel(maturityLevelsScoreMap, maturityLevels);
    }

    private Map<Long, Double> calculateMaturityLevelsMaxScore(List<Question> questions, Long qualityAttributeId) {
        Map<Long, Double> maturityLevelMaxScoreMap = new HashMap<>();
        for (Question question : questions) {
            for (QuestionImpact questionImpact : question.getImpacts()) {
                if (questionImpact.getQualityAttributeId().equals(qualityAttributeId))
                    maturityLevelMaxScoreMap.put(questionImpact.getMaturityLevelId(), maturityLevelMaxScoreMap
                        .getOrDefault(questionImpact.getMaturityLevelId(), 0.0) + questionImpact.getWeight());
            }
        }
        return maturityLevelMaxScoreMap;
    }

    private Long findQuestionAnswer(UUID assessmentResultId, Long questionId) {
        return loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.loadAnswerIdAndOptionId(
            assessmentResultId, questionId).map(LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result::answerOptionId
        ).orElse(null);
    }

    private static Map<Long, Double> calculatePercentageScoreMap(Map<Long, Double> maturityLevelEarnedScoreMap, Map<Long, Double> maturityLevelMaxScoreMap) {
        Map<Long, Double> qualityAttributeImpactScoreMap = new HashMap<>();
        for (Long maturityLevelId : maturityLevelEarnedScoreMap.keySet()) {
            qualityAttributeImpactScoreMap.put(maturityLevelId, maturityLevelEarnedScoreMap.getOrDefault(maturityLevelId, 0.0) / maturityLevelMaxScoreMap.get(maturityLevelId));
        }
        return qualityAttributeImpactScoreMap;
    }

    /**
     * This method sorts maturity level list of desired kit by its value.
     * Then iterates over level competences and compares through thresholds.
     * If no threshold fulfills, it will return first and least maturity level.
     */
    private MaturityLevel findAchievedMaturityLevel(Map<Long, Double> qualityAttImpactScoreMap, List<MaturityLevel> maturityLevels) {
        maturityLevels.sort(Comparator.comparingInt(MaturityLevel::getValue));
        MaturityLevel result = null;
        for (MaturityLevel maturityLevel : maturityLevels) {
            List<LevelCompetence> levelCompetences = maturityLevel.getLevelCompetences();
            if (levelCompetences.isEmpty()) {
                result = maturityLevel;
                continue;
            }
            boolean allCompetencesMatched = levelCompetences.stream()
                .allMatch(levelCompetence -> {
                    Long id = levelCompetence.getMaturityLevelCompetenceId();
                    return qualityAttImpactScoreMap.containsKey(id) && qualityAttImpactScoreMap.get(id) >= levelCompetence.getValue();
                });

            if (allCompetencesMatched)
                result = maturityLevel;
            else
                break;
        }
        return result;
    }
}
