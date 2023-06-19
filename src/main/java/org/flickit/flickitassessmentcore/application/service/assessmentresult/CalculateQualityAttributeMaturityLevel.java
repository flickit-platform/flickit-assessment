package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.*;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Slf4j
public class CalculateQualityAttributeMaturityLevel implements CalculateQualityAttributeMaturityLevelUseCase {

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
                    AnswerOption questionAnswer = findQuestionAnswer(assessmentResult, question);
                    Set<AnswerOptionImpact> answerOptionImpacts = loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOptionId(questionAnswer.getId());
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
        List<MaturityLevel> maturityLevels = new ArrayList<>(loadMaturityLevelByKit.loadMaturityLevelByKitId(qualityAttribute.getAssessmentSubject().getAssessmentKit().getId()));
        MaturityLevel qualityAttMaturityLevel = findMaturityLevelBasedOnCalculations(qualityAttributeImpactScoreMap, maturityLevels);

        return new QualityAttributeValue(
            UUID.randomUUID(),
            qualityAttribute,
            qualityAttMaturityLevel
        );
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
