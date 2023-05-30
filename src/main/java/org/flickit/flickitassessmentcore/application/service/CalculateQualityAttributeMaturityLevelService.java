package org.flickit.flickitassessmentcore.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class CalculateQualityAttributeMaturityLevelService implements CalculateQualityAttributeMaturityLevelUseCase {

    private final LoadQualityAttributePort loadQualityAttribute;
    private final SaveQualityAttributeValuePort saveQualityAttributeValue;
    private final SaveAssessmentResultPort saveAssessmentResult;

    @Override
    public MaturityLevel calculateQualityAttributeMaturityLevel(AssessmentResult assessmentResult, Long qualityAttributeId) {
        QualityAttribute qualityAttribute = loadQualityAttribute.loadQualityAttribute(qualityAttributeId);
        Map<Long, Integer> maturityLevelValueSumMap = new HashMap<>();
        Map<Long, Integer> maturityLevelValueCountMap = new HashMap<>();
        qualityAttribute.getAssessmentSubject().getQuestionnaires().forEach(questionnaire -> {
            questionnaire.getQuestions().forEach(question -> {
                Set<QualityAttribute> qualityAttributes = question.getQualityAttributes();
                for (QualityAttribute qualityAttribute1 : qualityAttributes) {
                    if (qualityAttribute1.getId().equals(qualityAttribute.getId())) {
                        AnswerOption questionAnswer = findQuestionAnswer(assessmentResult, question);
                        Set<AnswerOptionImpact> answerOptionImpacts = questionAnswer.getAnswerOptionImpacts();
                        for (AnswerOptionImpact impact : answerOptionImpacts) {
                            if (impact.getOption().getId().equals(questionAnswer.getId())) {
                                QuestionImpact questionImpact = impact.getImpact();
                                Integer value = (impact.getValue().intValueExact()) * impact.getImpact().getWeight();
                                maturityLevelValueSumMap.put(questionImpact.getMaturityLevel().getId(), maturityLevelValueSumMap.getOrDefault(questionImpact, 0) + value);
                                maturityLevelValueCountMap.put(questionImpact.getMaturityLevel().getId(), maturityLevelValueCountMap.getOrDefault(questionImpact, 0) + impact.getImpact().getWeight());
                            }
                        }
                    }
                }
            });
        });
        Map<Long, Integer> qualityAttributeImpactScoreMap = new HashMap<>();
        for (Long maturityLevelId : maturityLevelValueSumMap.keySet()) {
            qualityAttributeImpactScoreMap.put(maturityLevelId, maturityLevelValueSumMap.get(maturityLevelId) / maturityLevelValueCountMap.get(maturityLevelId));
        }
        List<MaturityLevel> maturityLevels = qualityAttribute.getAssessmentSubject().getAssessmentKit().getMaturityLevels().stream().toList();
        MaturityLevel qualityAttributeMaturityLevel = findMaturityLevelBasedOnCalculations(qualityAttributeImpactScoreMap, maturityLevels);
        saveQualityAttributeValue(assessmentResult, qualityAttribute, qualityAttributeMaturityLevel);
        saveAssessmentResult(assessmentResult, qualityAttributeId, qualityAttributeMaturityLevel);

        return qualityAttributeMaturityLevel;
    }

    private void saveQualityAttributeValue(AssessmentResult assessmentResult, QualityAttribute qualityAttribute, MaturityLevel qualityAttributeMaturityLevel) {
        QualityAttributeValue qualityAttributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            assessmentResult,
            qualityAttribute,
            qualityAttributeMaturityLevel
        );
        saveQualityAttributeValue.saveQualityAttributeValue(qualityAttributeValue);
    }

    private void saveAssessmentResult(AssessmentResult assessmentResult, Long qualityAttributeId, MaturityLevel qualityAttributeMaturityLevel) {
        List<QualityAttributeValue> qualityAttributeValues = assessmentResult.getQualityAttributeValues();
        for (QualityAttributeValue qualityAttributeValue1 : qualityAttributeValues) {
            if (qualityAttributeValue1.getQualityAttribute().getId().equals(qualityAttributeId)) {
                qualityAttributeValue1.setMaturityLevel(qualityAttributeMaturityLevel);
            }
        }
        saveAssessmentResult.saveAssessmentResult(assessmentResult);
    }

    private AnswerOption findQuestionAnswer(AssessmentResult assessmentResult, Question question) {
        List<Answer> answers = assessmentResult.getAnswers();
        for (Answer answer : answers) {
            if (answer.getQuestion().getId().equals(question.getId())) {
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
    private MaturityLevel findMaturityLevelBasedOnCalculations(Map<Long, Integer> qualityAttributeImpactScoreMap, List<MaturityLevel> maturityLevels) {
        MaturityLevel result = maturityLevels.get(0);
        maturityLevels.sort(Comparator.comparingInt(MaturityLevel::getValue));
        for (MaturityLevel maturityLevel : maturityLevels) {
            List<LevelCompetence> levelCompetences = maturityLevel.getLevelCompetences().stream().toList();
            for (LevelCompetence levelCompetence : levelCompetences) {
                Long id = levelCompetence.getMaturityLevelCompetence().getId();
                if (qualityAttributeImpactScoreMap.containsKey(id) && qualityAttributeImpactScoreMap.get(id) >= levelCompetence.getValue()) {
                    Optional<MaturityLevel> resultMaturityLevel = maturityLevels.stream().filter(ml -> ml.getId().equals(id)).findFirst();
                    if (resultMaturityLevel.isPresent()) {
                        result = resultMaturityLevel.get();
                    }
                } else {
                    break;
                }
            }
        }
        return result;
    }

}
