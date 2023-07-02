package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Component
@Slf4j
public class CalculateQualityAttributeMaturityLevel {

    private final LoadQuestionsByQualityAttributePort loadQuestionsByQAId;
    private final LoadAnswerOptionImpactsByAnswerOptionPort loadAnswerOptionImpactsByAnswerOption;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKit;
    private final LoadAnswersByResultPort loadAnswersByResult;
    private final LoadLevelCompetenceByMaturityLevelPort loadLevelCompetenceByMaturityLevel;

    public QualityAttributeValue calculateQualityAttributeMaturityLevel(AssessmentResult assessmentResult, QualityAttribute qualityAttribute) {
        Set<Question> questions = loadQuestionsByQAId.loadQuestionsByQualityAttributeId(qualityAttribute.getId());
        Map<Long, Integer> maturityLevelValueSumMap = new HashMap<>();
        Map<Long, Integer> maturityLevelValueCountMap = new HashMap<>();
        for (Question question : questions) {
            Set<QualityAttribute> qualityAttributes = question.getQualityAttributes();
            for (QualityAttribute qualityAttribute1 : qualityAttributes) {
                if (qualityAttribute1.getId().equals(qualityAttribute.getId())) {
                    Long questionAnswerId = findQuestionAnswer(assessmentResult, question);
                    Set<AnswerOptionImpact> answerOptionImpacts = loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOptionId(questionAnswerId);
                    for (AnswerOptionImpact impact : answerOptionImpacts) {
                        if (impact.getOption().getId().equals(questionAnswerId)) {
                            QuestionImpact questionImpact = impact.getImpact();
                            Integer value = impact.getValue().intValueExact() * impact.getImpact().getWeight();
                            Long maturityLevelId = questionImpact.getMaturityLevel().getId();
                            log.warn("Question: [{}] with Option: [{}] as answer, has value: [{}], on ml: [{}]",
                                question.getTitle(), questionAnswerId, value, maturityLevelId);
                            maturityLevelValueSumMap.put(maturityLevelId, maturityLevelValueSumMap.getOrDefault(maturityLevelId, 0) + value);
                            maturityLevelValueCountMap.put(maturityLevelId, maturityLevelValueCountMap.getOrDefault(maturityLevelId, 0) + impact.getImpact().getWeight());
                        }
                    }
                }
            }
        }
        Map<Long, Integer> qualityAttributeImpactScoreMap = new HashMap<>();
        for (Long maturityLevelId : maturityLevelValueSumMap.keySet()) {
            qualityAttributeImpactScoreMap.put(maturityLevelId, maturityLevelValueSumMap.get(maturityLevelId) / maturityLevelValueCountMap.get(maturityLevelId));
        }
        List<MaturityLevel> maturityLevels = new ArrayList<>(loadMaturityLevelByKit.loadMaturityLevelByKitId(qualityAttribute.getAssessmentSubject().getAssessmentKit().getId()));
        MaturityLevel qualityAttMaturityLevel = findMaturityLevelBasedOnCalculations(qualityAttributeImpactScoreMap, maturityLevels);

        return new QualityAttributeValue(
            UUID.randomUUID(),
            qualityAttribute,
            qualityAttMaturityLevel
        );
    }

    private Long findQuestionAnswer(AssessmentResult assessmentResult, Question question) {
        List<Answer> answers = new ArrayList<>(loadAnswersByResult.loadAnswersByResultId(assessmentResult.getId()));
        for (Answer answer : answers) {
            if (answer.getQuestionId() != null && answer.getQuestionId().equals(question.getId())) {
                return answer.getOptionId();
            }
        }
        throw new ResourceNotFoundException(ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ANSWER_NOT_FOUND_MESSAGE);
    }

    /**
     * This method sorts maturity level list of desired profile by its value.
     * Then iterates over level competences and compares through thresholds.
     * If no threshold fulfills, it will return first maturity level.
     */
    private MaturityLevel findMaturityLevelBasedOnCalculations(Map<Long, Integer> qualityAttImpactScoreMap, List<MaturityLevel> maturityLevels) {
        maturityLevels.sort(Comparator.comparing(MaturityLevel::getValue));
        MaturityLevel result = maturityLevels.get(0);
        maturityLevels.sort(Comparator.comparingInt(MaturityLevel::getValue));
        for (MaturityLevel maturityLevel : maturityLevels) {
            List<LevelCompetence> levelCompetences = new ArrayList<>(loadLevelCompetenceByMaturityLevel.loadLevelCompetenceByMaturityLevelId(maturityLevel.getId()));
            for (LevelCompetence levelCompetence : levelCompetences) {
                Long id = levelCompetence.getMaturityLevelCompetence().getId();
                if (qualityAttImpactScoreMap.containsKey(id) && qualityAttImpactScoreMap.get(id) >= levelCompetence.getValue()) {
                    Optional<MaturityLevel> resultMaturityLevel = maturityLevels.stream().filter(ml -> ml.getId().equals(id)).findFirst();
                    result = resultMaturityLevel.orElseGet(() -> maturityLevels.get(0));
                } else {
                    break;
                }
            }
        }
        return result;
    }
}
