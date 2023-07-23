package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Component
@Slf4j
public class CalculateQualityAttributeMaturityLevel {

    private final LoadQuestionsByQualityAttributePort loadQuestionsByQualityAttributePort;
    private final LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;
    private final LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
    private final LoadLevelCompetenceByMaturityLevelPort loadLevelCompetenceByMaturityLevelPort;
    private final LoadQuestionImpactPort loadQuestionImpactPort;

    public MaturityLevel calculate(UUID assessmentResultId, Long qualityAttributeId, Long assessmentKitId) {
        Set<Question> questions = loadQuestionsByQualityAttributePort.loadByQualityAttributeId(new LoadQuestionsByQualityAttributePort.Param(qualityAttributeId)).questions();
        Map<Long, Integer> maturityLevelValueSumMap = new HashMap<>();
        Map<Long, Integer> maturityLevelValueCountMap = new HashMap<>();
        for (Question question : questions) {
            Long answerOptionId = findQuestionAnswer(assessmentResultId, question.getId());
            if (answerOptionId != null) {
                Set<AnswerOptionImpact> answerOptionImpacts = loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort
                    .loadByAnswerOptionIdAndQualityAttributeId(answerOptionId, qualityAttributeId).optionImpacts();
                for (AnswerOptionImpact impact : answerOptionImpacts) {
                    if (impact.getOptionId().equals(answerOptionId)) {
                        Long questionImpactId = impact.getQuestionImpactId();
                        QuestionImpact questionImpact = loadQuestionImpactPort.load(questionImpactId).questionImpact();
                        Long maturityLevelId = calculateGainedScoreValue(maturityLevelValueSumMap, question, answerOptionId, impact, questionImpact);
                        calculateRealScore(maturityLevelValueCountMap, questionImpact, maturityLevelId);
                    }
                }
            }
        }
        Map<Long, Integer> qualityAttributeImpactScoreMap = calculateScoreMap(maturityLevelValueSumMap, maturityLevelValueCountMap);
        // We have to create a new list, because the output of called method is immutable list, so we can't do anything on it further.
        List<MaturityLevel> maturityLevels = new ArrayList<>(loadMaturityLevelByKitPort.loadByKitId(assessmentKitId).maturityLevels());
        MaturityLevel qualityAttMaturityLevel = findMaturityLevelBasedOnCalculations(qualityAttributeImpactScoreMap, maturityLevels);

        return qualityAttMaturityLevel;
    }

    private Long findQuestionAnswer(UUID assessmentResultId, Long questionId) {
        return loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.loadAnswerIdAndOptionId(
            assessmentResultId, questionId
        ).get().answerOptionId();
    }

    private static Long calculateGainedScoreValue(Map<Long, Integer> maturityLevelValueSumMap, Question question, Long answerOptionId, AnswerOptionImpact impact, QuestionImpact questionImpact) {
        Integer value = impact.getValue().setScale(0, RoundingMode.HALF_UP).intValue() * questionImpact.getWeight();
        Long maturityLevelId = questionImpact.getMaturityLevelId();
        log.debug("Question: [{}] with Option: [{}] as answer, has value: [{}], on ml: [{}]",
            question.getTitle(), answerOptionId, value, maturityLevelId);
        maturityLevelValueSumMap.put(maturityLevelId, maturityLevelValueSumMap.getOrDefault(maturityLevelId, 0) + value);
        return maturityLevelId;
    }

    private static void calculateRealScore(Map<Long, Integer> maturityLevelValueCountMap, QuestionImpact questionImpact, Long maturityLevelId) {
        maturityLevelValueCountMap.put(maturityLevelId, maturityLevelValueCountMap.getOrDefault(maturityLevelId, 0) + questionImpact.getWeight());
    }

    private static Map<Long, Integer> calculateScoreMap(Map<Long, Integer> maturityLevelValueSumMap, Map<Long, Integer> maturityLevelValueCountMap) {
        Map<Long, Integer> qualityAttributeImpactScoreMap = new HashMap<>();
        for (Long maturityLevelId : maturityLevelValueSumMap.keySet()) {
            qualityAttributeImpactScoreMap.put(maturityLevelId, maturityLevelValueSumMap.get(maturityLevelId) / maturityLevelValueCountMap.get(maturityLevelId));
        }
        return qualityAttributeImpactScoreMap;
    }

    /**
     * This method sorts maturity level list of desired kit by its value.
     * Then iterates over level competences and compares through thresholds.
     * If no threshold fulfills, it will return first and least maturity level.
     */
    private MaturityLevel findMaturityLevelBasedOnCalculations(Map<Long, Integer> qualityAttImpactScoreMap, List<MaturityLevel> maturityLevels) {
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
