package org.flickit.flickitassessmentcore.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.CalculateQAMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.in.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.*;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateQualityAttributeMaturityLevelService implements CalculateQualityAttributeMaturityLevelUseCase {

    private final LoadQualityAttributePort loadQualityAttribute;
    private final LoadQuestionsByQAIdPort loadQuestionsByQAId;
    private final LoadAnswerOptionImpactsByAnswerOptionPort loadAnswerOptionImpactsByAnswerOption;
    private final LoadMLByKitPort loadMLByKit;
    private final LoadQualityAttributeValuesByResultPort loadQualityAttributeValuesByResult;
    private final LoadAnswersByResultPort loadAnswersByResult;
    private final LoadLevelCompetenceByMLPort loadLCByML;
    private final SaveQualityAttributeValuePort saveQualityAttributeValue;
    private final SaveAssessmentResultPort saveAssessmentResult;
    private final LoadAssessmentResultPort loadAssessmentResult;

    @Override
    public MaturityLevel calculateQualityAttributeMaturityLevel(CalculateQAMaturityLevelCommand command) {
        Long qualityAttributeId = command.getQaId();
        AssessmentResult assessmentResult = loadAssessmentResult.loadResult(command.getResultId());
        QualityAttribute qualityAttribute = loadQualityAttribute.loadQualityAttribute(qualityAttributeId);
        Set<Question> questions = loadQuestionsByQAId.loadQuestionsByQualityAttributeId(qualityAttributeId);
        Map<Long, Integer> maturityLevelValueSumMap = new HashMap<>();
        Map<Long, Integer> maturityLevelValueCountMap = new HashMap<>();
        for (Question question : questions) {
            Set<QualityAttribute> qualityAttributes = question.getQualityAttributes();
            for (QualityAttribute qualityAttribute1 : qualityAttributes) {
                if (qualityAttribute1.getId().equals(qualityAttribute.getId())) {
                    AnswerOption questionAnswer = findQuestionAnswer(assessmentResult, question);
                    Set<AnswerOptionImpact> answerOptionImpacts = loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOption(questionAnswer.getId());
                    for (AnswerOptionImpact impact : answerOptionImpacts) {
                        if (impact.getOption().getId().equals(questionAnswer.getId())) {
                            QuestionImpact questionImpact = impact.getImpact();
                            Integer value = impact.getValue().intValueExact() * impact.getImpact().getWeight();
                            Long maturityLevelId = questionImpact.getMaturityLevel().getId();
                            log.warn("Question: [{}] with Option: [{}] as answer, has value: [{}], on ml: [{}]",
                                question.getTitle(), questionAnswer.getId(), value, maturityLevelId);
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
        List<MaturityLevel> maturityLevels = new ArrayList<>(loadMLByKit.loadMLByKitId(qualityAttribute.getAssessmentSubject().getAssessmentKit().getId()));;
        MaturityLevel qualityAttMaturityLevel = findMaturityLevelBasedOnCalculations(qualityAttributeImpactScoreMap, maturityLevels);
        saveQualityAttributeValue(assessmentResult, qualityAttribute, qualityAttMaturityLevel);

        // TODO
        // We should keep the previous results
        // Change result if it really has been changed (if result is same as calculated so don't touch it)
        saveAssessmentResult(assessmentResult, qualityAttributeId, qualityAttMaturityLevel);

        return qualityAttMaturityLevel;
    }

    private void saveQualityAttributeValue(AssessmentResult assessmentResult, QualityAttribute qualityAttribute, MaturityLevel qualityAttMaturityLevel) {
        QualityAttributeValue qualityAttributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            assessmentResult,
            qualityAttribute,
            qualityAttMaturityLevel
        );
        saveQualityAttributeValue.saveQualityAttributeValue(qualityAttributeValue);
    }

    private void saveAssessmentResult(AssessmentResult assessmentResult, Long qualityAttributeId, MaturityLevel qualityAttMaturityLevel) {
        List<QualityAttributeValue> qualityAttributeValues = new ArrayList<>(loadQualityAttributeValuesByResult.loadQualityAttributeValuesByResultId(assessmentResult.getId()));
        for (QualityAttributeValue qualityAttributeValue1 : qualityAttributeValues) {
            if (qualityAttributeValue1.getQualityAttribute().getId().equals(qualityAttributeId)) {
                qualityAttributeValue1.setMaturityLevel(qualityAttMaturityLevel);
            }
        }
        saveAssessmentResult.saveAssessmentResult(assessmentResult);
    }

    private AnswerOption findQuestionAnswer(AssessmentResult assessmentResult, Question question) {
        List<Answer> answers = new ArrayList<>(loadAnswersByResult.loadAnswersByResultId(assessmentResult.getId()));
        for (Answer answer : answers) {
            if (answer.getQuestion() != null && answer.getQuestion().getId().equals(question.getId())) {
                return answer.getAnswerOption();
            }
        }
        throw new NoAnswerFoundException("Question «" + question.getTitle() + " » has no answer!");
    }

    /**
     * This method sorts maturity level list of desired profile by its value.
     * Then iterates over level competences and compares through thresholds.
     * If no threshold fulfills, it will return first maturity level.
     */
    private MaturityLevel findMaturityLevelBasedOnCalculations(Map<Long, Integer> qualityAttImpactScoreMap, List<MaturityLevel> maturityLevels) {
        MaturityLevel result = maturityLevels.get(0);
        maturityLevels.sort(Comparator.comparingInt(MaturityLevel::getValue));
        for (MaturityLevel maturityLevel : maturityLevels) {
            List<LevelCompetence> levelCompetences = new ArrayList<>(loadLCByML.loadLevelCompetenceByMLId(maturityLevel.getId()));
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
