package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
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

    private final LoadQuestionsByQualityAttributePort loadQuestionsByQAId;
    private final LoadAnswerOptionImpactsByAnswerOptionPort loadAnswerOptionImpactsByAnswerOption;
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKit;
    private final LoadAnswersByResultPort loadAnswersByResult;
    private final LoadLevelCompetenceByMaturityLevelPort loadLevelCompetenceByMaturityLevel;
    private final LoadQuestionImpactPort loadQuestionImpact;

    public MaturityLevel calculateQualityAttributeMaturityLevel(AssessmentResult assessmentResult, QualityAttribute qualityAttribute, Long assessmentKitId) {
        Set<Question> questions = loadQuestionsByQAId.loadQuestionsByQualityAttributeId(new LoadQuestionsByQualityAttributePort.Param(qualityAttribute.getId())).questions();
        Map<Long, Integer> maturityLevelValueSumMap = new HashMap<>();
        Map<Long, Integer> maturityLevelValueCountMap = new HashMap<>();
        for (Question question : questions) {
            Set<QualityAttribute> qualityAttributes = question.getQualityAttributes();
            for (QualityAttribute qualityAttribute1 : qualityAttributes) {
                if (qualityAttribute1.getId().equals(qualityAttribute.getId())) {
                    Long questionAnswerId = findQuestionAnswer(assessmentResult, question);
                    if (questionAnswerId != null) {
                        Set<AnswerOptionImpact> answerOptionImpacts = loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOptionId(new LoadAnswerOptionImpactsByAnswerOptionPort.Param(questionAnswerId)).optionImpacts();
                        for (AnswerOptionImpact impact : answerOptionImpacts) {
                            if (impact.getOptionId().equals(questionAnswerId)) {
                                Long questionImpactId = impact.getQuestionImapctId();
                                QuestionImpact questionImpact = loadQuestionImpact.loadQuestionImpact(new LoadQuestionImpactPort.Param(questionImpactId)).questionImpact();
                                Integer value = impact.getValue().setScale(0, RoundingMode.HALF_UP).intValue() * questionImpact.getWeight();
                                Long maturityLevelId = questionImpact.getMaturityLevelId();
                                log.debug("Question: [{}] with Option: [{}] as answer, has value: [{}], on ml: [{}]",
                                    question.getTitle(), questionAnswerId, value, maturityLevelId);
                                maturityLevelValueSumMap.put(maturityLevelId, maturityLevelValueSumMap.getOrDefault(maturityLevelId, 0) + value);
                                maturityLevelValueCountMap.put(maturityLevelId, maturityLevelValueCountMap.getOrDefault(maturityLevelId, 0) + questionImpact.getWeight());
                            }
                        }
                    }
                }
            }
        }
        Map<Long, Integer> qualityAttributeImpactScoreMap = new HashMap<>();
        for (Long maturityLevelId : maturityLevelValueSumMap.keySet()) {
            qualityAttributeImpactScoreMap.put(maturityLevelId, maturityLevelValueSumMap.get(maturityLevelId) / maturityLevelValueCountMap.get(maturityLevelId));
        }
        List<MaturityLevel> maturityLevels = new ArrayList<>(loadMaturityLevelByKit.loadMaturityLevelByKitId(
            new LoadMaturityLevelByKitPort.Param(
                assessmentKitId))
            .maturityLevels());
        MaturityLevel qualityAttMaturityLevel = findMaturityLevelBasedOnCalculations(qualityAttributeImpactScoreMap, maturityLevels);

        return qualityAttMaturityLevel;
    }

    private Long findQuestionAnswer(AssessmentResult assessmentResult, Question question) {
        List<Answer> answers = new ArrayList<>(loadAnswersByResult.loadAnswersByResultId(assessmentResult.getId()));
        for (Answer answer : answers) {
            if (answer.getQuestionId() != null && answer.getQuestionId().equals(question.getId())) {
                return answer.getOptionId();
            }
        }
        return null;
//        throw new ResourceNotFoundException(ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ANSWER_NOT_FOUND_MESSAGE);
    }

    /**
     * This method sorts maturity level list of desired kit by its value.
     * Then iterates over level competences and compares through thresholds.
     * If no threshold fulfills, it will return first and least maturity level.
     */
    private MaturityLevel findMaturityLevelBasedOnCalculations(Map<Long, Integer> qualityAttImpactScoreMap, List<MaturityLevel> maturityLevels) {
        maturityLevels.sort(Comparator.comparing(MaturityLevel::getValue));
        MaturityLevel result = maturityLevels.get(0);
        maturityLevels.sort(Comparator.comparingInt(MaturityLevel::getValue));
        for (MaturityLevel maturityLevel : maturityLevels) {
            List<LevelCompetence> levelCompetences = new ArrayList<>(loadLevelCompetenceByMaturityLevel.loadLevelCompetenceByMaturityLevelId(new LoadLevelCompetenceByMaturityLevelPort.Param(maturityLevel.getId())).levelCompetences());
            for (LevelCompetence levelCompetence : levelCompetences) {
                Long id = levelCompetence.getMaturityLevelCompetenceId();
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
