package org.flickit.assessment.advice.adapter.out.calculation;

import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.EffectiveQuestionOnAdviceView;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.LOAD_ADVICE_CALC_INFO_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class LoadAdviceCalculationAdapter implements LoadAdviceCalculationInfoPort {

    private final QuestionJpaRepository questionRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;
    private final QualityAttributeValueJpaRepository attributeValueRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    private static final double DEFAULT_ATTRIBUTE_MATURITY_SCORE = 0.0;
    private static final int DEFAULT_QUESTION_COST = 1;

    @Override
    public Plan loadAdviceCalculationInfo(UUID assessmentId, Map<Long, Long> attrIdToLevelId) {
        List<AttributeLevelScore> attributeLevelScores = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for (Map.Entry<Long, Long> attrIdToLevelIdEntry: attrIdToLevelId.entrySet()) {
            Long attributeId = attrIdToLevelIdEntry.getKey();
            Long maturityLevelId = attrIdToLevelIdEntry.getValue();

            List<LevelCompetenceJpaEntity> levelCompetenceEntities =
                levelCompetenceRepository.findByAffectedLevelId(maturityLevelId);
            for (LevelCompetenceJpaEntity levelCompetenceEntity: levelCompetenceEntities) {
                Long effectiveLevelId = levelCompetenceEntity.getEffectiveLevel().getId();
                var assessmentResultJpaEntity = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(LOAD_ADVICE_CALC_INFO_ASSESSMENT_RESULT_NOT_FOUND));
                QualityAttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByQualityAttributeIdAndAssessmentResult_Id(attributeId, assessmentResultJpaEntity.getId());

                Double gainedScorePercentage = attributeMaturityScoreRepository
                    .findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), effectiveLevelId)
                    .map(AttributeMaturityScoreJpaEntity::getScore)
                    .orElse(DEFAULT_ATTRIBUTE_MATURITY_SCORE);

                List<EffectiveQuestionOnAdviceView> effectiveQuestions =
                    questionRepository.findQuestionsEffectedOnAdvice(assessmentId, attributeId, effectiveLevelId);

                Map<Long, Integer> effectiveQuestionIdToQuestionImpact = mapOfEffectiveQuestionIdToQuestionImpact(effectiveQuestions);
                int totalScore = calculateTotalScore(effectiveQuestionIdToQuestionImpact);
                double gainedScore = totalScore * (gainedScorePercentage/100.0);
                double requiredScore = totalScore * (levelCompetenceEntity.getValue()/100.0);
                AttributeLevelScore attributeLevelScore =
                    new AttributeLevelScore(gainedScore, requiredScore, attributeId, effectiveLevelId);
                attributeLevelScores.add(attributeLevelScore);

                Map<Long, List<EffectiveQuestionOption>> effectiveQuestionIdToOptions = mapOfEffectiveQuestionIdToOptions(effectiveQuestions);
                Map<Long, Integer> effectiveQuestionIdToQuestionAnswer = mapOfEffectiveQuestionIdToQuestionAnswer(effectiveQuestions);
                effectiveQuestionIdToOptions.forEach((effectiveQuestionId, effectiveOptions) -> {
                    Optional<Question> possibleQuestion = questions.stream()
                        .filter(e -> e.getId() == effectiveQuestionId)
                        .findFirst();
                    if (possibleQuestion.isPresent()) {
                        Question existedQuestion = possibleQuestion.get();
                        addAttrLevelScoreToExistedQuestion(effectiveOptions, existedQuestion, attributeLevelScore);
                    } else {
                        Integer answeredOptionIndex = effectiveQuestionIdToQuestionAnswer.get(effectiveQuestionId);
                        Question question = mapToQuestion(effectiveQuestionId, answeredOptionIndex, effectiveOptions, attributeLevelScore);
                        questions.add(question);
                    }
                });
            }
        }
        return new Plan(attributeLevelScores, questions);
    }

    private static Map<Long, Integer> mapOfEffectiveQuestionIdToQuestionImpact(List<EffectiveQuestionOnAdviceView> effectiveQuestions) {
        Map<Long, List<EffectiveQuestionOnAdviceView>> questionInfoGroupedById = effectiveQuestions.stream()
            .collect(Collectors.groupingBy(EffectiveQuestionOnAdviceView::getEffectiveQuestionId));
        Map<Long, Integer> questionIdToQuestionImpact = new HashMap<>();
        questionInfoGroupedById.forEach((questionId, questionInfo) -> {
            Integer questionImpactWeight = questionInfo.get(0).getEffectiveQuestionImpactWeight();
            questionIdToQuestionImpact.put(questionId, questionImpactWeight);
        });
        return questionIdToQuestionImpact;
    }

    private static Map<Long, Integer> mapOfEffectiveQuestionIdToQuestionAnswer(List<EffectiveQuestionOnAdviceView> effectiveQuestions) {
        Map<Long, List<EffectiveQuestionOnAdviceView>> questionInfoGroupedById = effectiveQuestions.stream()
            .collect(Collectors.groupingBy(EffectiveQuestionOnAdviceView::getEffectiveQuestionId));
        Map<Long, Integer> questionIdToQuestionAnswer = new HashMap<>();
        questionInfoGroupedById.forEach((questionId, questionInfo) -> {
            Integer currentOptionIndex = questionInfo.get(0).getEffectiveAnsweredOptionIndex();
            if (currentOptionIndex != null) {
                currentOptionIndex -= 1;
            }
            questionIdToQuestionAnswer.put(questionId, currentOptionIndex);
        });
        return questionIdToQuestionAnswer;
    }

    private static Map<Long, List<EffectiveQuestionOption>> mapOfEffectiveQuestionIdToOptions(List<EffectiveQuestionOnAdviceView> effectiveQuestions) {
        Map<Long, List<EffectiveQuestionOnAdviceView>> questionInfoGroupedById = effectiveQuestions.stream()
            .collect(Collectors.groupingBy(EffectiveQuestionOnAdviceView::getEffectiveQuestionId));
        Map<Long, List<EffectiveQuestionOption>> questionIdToOptions = new HashMap<>();
        questionInfoGroupedById.forEach((questionId, questionInfo) -> {
            List<EffectiveQuestionOption> options = questionInfo.stream()
                .map(e -> new EffectiveQuestionOption(e.getEffectiveOptionId(),
                    e.getEffectiveOptionIndex(),
                    e.getEffectiveOptionImpactValue()))
                .toList();

            questionIdToOptions.put(questionId, options);
        });

        return questionIdToOptions;
    }

    private static int calculateTotalScore(Map<Long, Integer> questionIdToQuestionImpact) {
        return questionIdToQuestionImpact.values()
            .stream()
            .reduce(0, Integer::sum);
    }

    private static void addAttrLevelScoreToExistedQuestion(List<EffectiveQuestionOption> options,
                                                           Question question,
                                                           AttributeLevelScore attributeLevelScore) {
        options.forEach(v -> {
            Option option = question.getOptions().stream()
                .filter(m -> m.getIndex() == v.effectiveOptionIndex)
                .findFirst()
                .get();
            option.getPromisedScores().put(attributeLevelScore, v.effectiveOptionImpactValue);
        });
    }

    private static Question mapToQuestion(Long effectiveQuestionId,
                                          Integer answeredOptionIndex,
                                          List<EffectiveQuestionOption> effectiveQuestionOptions,
                                          AttributeLevelScore attributeLevelScore) {
        List<Option> options = mapToOptions(effectiveQuestionOptions, attributeLevelScore);
        return new Question(effectiveQuestionId, DEFAULT_QUESTION_COST, options, answeredOptionIndex);
    }

    private static List<Option> mapToOptions(List<EffectiveQuestionOption> effectiveQuestionOptions,
                                             AttributeLevelScore attributeLevelScore) {
        return effectiveQuestionOptions.stream().map(e -> {
            double progress = (e.effectiveOptionIndex() - 1) * (1.0/(effectiveQuestionOptions.size() - 1));
            Map<AttributeLevelScore, Double> promisedScores = new HashMap<>();
            promisedScores.put(attributeLevelScore, e.effectiveOptionImpactValue());
            return new Option(e.effectiveOptionId(),
                e.effectiveOptionIndex(),
                promisedScores,
                progress,
                DEFAULT_QUESTION_COST);
        }).toList();
    }

    private record EffectiveQuestionOption(Long effectiveOptionId,
                                           Integer effectiveOptionIndex,
                                           double effectiveOptionImpactValue) {}
}
