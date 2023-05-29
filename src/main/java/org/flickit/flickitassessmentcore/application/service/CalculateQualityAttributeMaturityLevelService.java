package org.flickit.flickitassessmentcore.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadQualityAttributePort;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class CalculateQualityAttributeMaturityLevelService implements CalculateQualityAttributeMaturityLevelUseCase {

    private final LoadQualityAttributePort loadQualityAttribute;

    @Override
    public MaturityLevel calculateQualityAttributeMaturityLevel(Set<AssessmentResult> assessmentResults, Long qualityAttributeId) {
        QualityAttribute qualityAttribute = loadQualityAttribute.loadQualityAttribute(qualityAttributeId);
        Map<Long, Integer> maturityLevelSumsMap = new HashMap<>();
        Map<Long, Integer> maturityLevelCountMap = new HashMap<>();
        qualityAttribute.getAssessmentSubject().getQuestionnaires().forEach(questionnaire -> {
            questionnaire.getQuestions().forEach(question -> {
                Set<QualityAttribute> qualityAttributes = question.getQualityAttributes();
                for (QualityAttribute qualityAttribute1 : qualityAttributes) {
                    if (qualityAttribute1.getId().equals(qualityAttribute.getId())) {
                        AnswerOption questionAnswer = findQuestionAnswer(assessmentResults, question);
                        Set<AnswerOptionImpact> answerOptionImpacts = questionAnswer.getAnswerOptionImpacts();
                        for (AnswerOptionImpact impact : answerOptionImpacts) {
                            if (impact.getOption().getId().equals(questionAnswer.getId())) {
                                QuestionImpact questionImpact = impact.getImpact();
                                Integer value = (impact.getValue().intValueExact()) * impact.getImpact().getWeight();
                                maturityLevelSumsMap.put(questionImpact.getMaturityLevel().getId(), maturityLevelSumsMap.getOrDefault(questionImpact, 0) + value);
                                maturityLevelCountMap.put(questionImpact.getMaturityLevel().getId(), maturityLevelCountMap.getOrDefault(questionImpact, 0) + 1);
                            }
                        }
                    }
                }
            });
        });
        Map<Long, Integer> qualityAttributeImpactScoreMap = new HashMap<>();
        for (Long maturityLevelId : maturityLevelSumsMap.keySet()) {
            qualityAttributeImpactScoreMap.put(maturityLevelId, maturityLevelSumsMap.get(maturityLevelId) / maturityLevelCountMap.get(maturityLevelId));
        }
        List<MaturityLevel> maturityLevels = qualityAttribute.getAssessmentSubject().getAssessmentProfile().getMaturityLevels().stream().toList();
        MaturityLevel qualityAttributeMaturityLevel = findMaturityLevel(qualityAttributeImpactScoreMap, maturityLevels);
        return qualityAttributeMaturityLevel;
    }

    private AnswerOption findQuestionAnswer(Set<AssessmentResult> assessmentResults, Question question) {
        for (AssessmentResult result : assessmentResults) {
            List<Answer> answers = result.getAnswers();
            for (Answer answer : answers) {
                if (answer.getQuestion().getId().equals(question.getId())) {
                    return answer.getAnswerOption();
                }
            }
        }
        throw new NoAnswerFoundException("Question «" + question.getTitle() + " » has no answer!");
    }

    /**
     * This method sorts maturity level list of desired profile by its value.
     * Then iterates over level competences and compares through thresholds.
     * If no threshold fulfills, it will return first maturity level.
     */
    private MaturityLevel findMaturityLevel(Map<Long, Integer> qualityAttributeImpactScoreMap, List<MaturityLevel> maturityLevels) {
        MaturityLevel result = maturityLevels.get(0);
        maturityLevels.sort(Comparator.comparingInt(MaturityLevel::getValue));
        for (MaturityLevel maturityLevel : maturityLevels) {
            List<LevelCompetence> levelCompetences = maturityLevel.getLevelCompetences().stream().collect(Collectors.toList());
            for (LevelCompetence levelCompetence : levelCompetences) {
                Long id = levelCompetence.getMaturityLevelCompetence().getId();
                if (qualityAttributeImpactScoreMap.containsKey(id) && qualityAttributeImpactScoreMap.get(id) >= levelCompetence.getValue()) {
                    result = maturityLevels.stream().filter(ml -> ml.getId().equals(id)).findFirst().get();
                } else {
                    break;
                }
            }
        }
        return result;
    }

}
