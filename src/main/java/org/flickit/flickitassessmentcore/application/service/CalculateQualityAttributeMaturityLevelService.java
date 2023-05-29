package org.flickit.flickitassessmentcore.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadQualityAttributePort;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class CalculateQualityAttributeMaturityLevelService implements CalculateQualityAttributeMaturityLevelUseCase {

    private final LoadQualityAttributePort loadQualityAttribute;

    @Override
    public MaturityLevel calculateQualityAttributeMaturityLevel(Set<AssessmentResult> assessmentResults, Long qualityAttributeId) {
        QualityAttribute qualityAttribute = loadQualityAttribute.loadQualityAttribute(qualityAttributeId);
        Map<QuestionImpact, Integer> impactSumsMap = new HashMap<>();
        Map<QuestionImpact, Integer> impactCountMap = new HashMap<>();
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
                                impactSumsMap.put(questionImpact, impactSumsMap.getOrDefault(questionImpact, 0) + value);
                                impactCountMap.put(questionImpact, impactCountMap.getOrDefault(questionImpact, 0) + 1);
                            }
                        }
                    }
                }
            });
        });
        Map<QuestionImpact, Integer> qualityAttributeImpactScoreMap = new HashMap<>();
        for (QuestionImpact questionImpact : impactSumsMap.keySet()) {
            qualityAttributeImpactScoreMap.put(questionImpact, impactSumsMap.get(questionImpact) / impactCountMap.get(questionImpact));
        }
        // NOW CHECK THE LEVELS AND FIND THE MATURITY LEVEL
        return new MaturityLevel();
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

}
