package org.flickit.assessment.advice.adapter.out.calculation;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.*;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.ImprovableImpactfulQuestionView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.advice.application.service.advice.PlanConstraintProvider.SOFT_SCORE_FACTOR;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class LoadAdviceCalculationInfoAdapter implements LoadAdviceCalculationInfoPort {

    private final QuestionJpaRepository questionRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;
    private final AttributeValueJpaRepository attributeValueRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    private static final double DEFAULT_ATTRIBUTE_MATURITY_SCORE = 0.0;
    private static final int DEFAULT_QUESTION_COST = 1;

    @Override
    public Plan loadAdviceCalculationInfo(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        List<AttributeLevelScore> attributeLevelScores = new ArrayList<>();
        Map<Long, Question> idToQuestions = new HashMap<>();
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND));

        for (AttributeLevelTarget attributeLevelTarget : attributeLevelTargets) {
            Long attributeId = attributeLevelTarget.getAttributeId();
            Long maturityLevelId = attributeLevelTarget.getMaturityLevelId();

            List<LevelCompetenceJpaEntity> levelCompetenceEntities =
                levelCompetenceRepository.findByAffectedLevelIdAndKitVersionId(maturityLevelId, assessmentResult.getKitVersionId());
            for (LevelCompetenceJpaEntity levelCompetenceEntity : levelCompetenceEntities) {
                Long effectiveLevelId = levelCompetenceEntity.getEffectiveLevelId();
                AttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByAttributeIdAndAssessmentResultId(attributeId, assessmentResult.getId());

                Double gainedScorePercentage = attributeMaturityScoreRepository
                    .findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), effectiveLevelId)
                    .map(AttributeMaturityScoreJpaEntity::getScore)
                    .orElse(DEFAULT_ATTRIBUTE_MATURITY_SCORE);

                List<ImprovableImpactfulQuestionView> impactfulQuestions =
                    questionRepository.findAdvisableImprovableImpactfulQuestions(assessmentResult.getId(), assessmentResult.getKitVersionId(), attributeId, effectiveLevelId);

                Map<Long, Integer> impactfulQuestionIdToQuestionImpact = mapImpactfulQuestionIdToWeight(impactfulQuestions);
                int totalScore = calculateTotalScore(impactfulQuestionIdToQuestionImpact);
                double gainedScore = totalScore * (gainedScorePercentage/100.0);
                double requiredScore = totalScore * (levelCompetenceEntity.getValue()/100.0);
                AttributeLevelScore attributeLevelScore =
                    new AttributeLevelScore(gainedScore, requiredScore, attributeId, effectiveLevelId);
                attributeLevelScores.add(attributeLevelScore);

                Map<Long, List<ImpactfulQuestionOption>> impactfulQuestionIdToOptions = mapImpactfulQuestionIdToOptions(impactfulQuestions);
                Map<Long, Integer> impactfulQuestionIdToQuestionAnswer = mapImpactfulQuestionIdToAnswer(impactfulQuestions);
                impactfulQuestionIdToOptions.forEach((impactfulQuestionId, impactfulOptions) -> {
                    if (idToQuestions.containsKey(impactfulQuestionId)) {
                        Question existedQuestion = idToQuestions.get(impactfulQuestionId);
                        addAttrLevelScoreToQuestionOptions(impactfulOptions, existedQuestion, attributeLevelScore);
                    } else {
                        Integer answeredOptionIndex = impactfulQuestionIdToQuestionAnswer.get(impactfulQuestionId);
                        Question question = mapToQuestion(impactfulQuestionId, answeredOptionIndex, impactfulOptions, attributeLevelScore);
                        idToQuestions.put(impactfulQuestionId, question);
                    }
                });
            }
        }
        return new Plan(attributeLevelScores, new ArrayList<>(idToQuestions.values()));
    }

    private Map<Long, Integer> mapImpactfulQuestionIdToWeight(List<ImprovableImpactfulQuestionView> impactfulQuestions) {
        Map<Long, Integer> questionIdToWeight = new HashMap<>();
        for (ImprovableImpactfulQuestionView question: impactfulQuestions) {
            Long questionId = question.getQuestionId();
            Integer weight = question.getQuestionImpactWeight();
            questionIdToWeight.putIfAbsent(questionId, weight);
        }
        return questionIdToWeight;
    }

    private Map<Long, List<ImpactfulQuestionOption>> mapImpactfulQuestionIdToOptions(List<ImprovableImpactfulQuestionView> impactfulQuestions) {
        return impactfulQuestions.stream()
            .collect(groupingBy(ImprovableImpactfulQuestionView::getQuestionId,
                mapping(
                    impactfulQuestion -> new ImpactfulQuestionOption(
                        impactfulQuestion.getOptionId(),
                        impactfulQuestion.getOptionIndex(),
                        impactfulQuestion.getOptionImpactValue() != null ?
                            impactfulQuestion.getOptionImpactValue() : impactfulQuestion.getOptionValue()
                    ),
                    toList()
                )));
    }

    private Map<Long, Integer> mapImpactfulQuestionIdToAnswer(List<ImprovableImpactfulQuestionView> impactfulQuestions) {
        Map<Long, Integer> questionIdToQuestionAnswer = new HashMap<>();
        for (ImprovableImpactfulQuestionView question: impactfulQuestions) {
            Long questionId = question.getQuestionId();
            Integer answeredOptionIndex = question.getAnsweredOptionIndex();
            if (answeredOptionIndex != null) {
                answeredOptionIndex -= 1;
            }
            questionIdToQuestionAnswer.putIfAbsent(questionId, answeredOptionIndex);
        }
        return questionIdToQuestionAnswer;
    }

    private int calculateTotalScore(Map<Long, Integer> questionIdToQuestionImpact) {
        return questionIdToQuestionImpact.values()
            .stream()
            .reduce(0, Integer::sum);
    }

    private void addAttrLevelScoreToQuestionOptions(List<ImpactfulQuestionOption> options,
                                                           Question question,
                                                           AttributeLevelScore attributeLevelScore) {
        Map<Long, ImpactfulQuestionOption> idToOption = options.stream()
            .collect(toMap(op -> op.impactfulOptionId, Function.identity()));
        question.getOptions().forEach(op -> {
            ImpactfulQuestionOption option = idToOption.get(op.getId());
            if (option != null)
                op.getPromisedScores().put(attributeLevelScore, option.impactfulOptionImpactValue);
        });
    }

    private Question mapToQuestion(Long impactfulQuestionId,
                                          Integer answeredOptionIndex,
                                          List<ImpactfulQuestionOption> impactfulQuestionOptions,
                                          AttributeLevelScore attributeLevelScore) {
        List<Option> options = mapToOptions(impactfulQuestionOptions, attributeLevelScore);
        return new Question(impactfulQuestionId, DEFAULT_QUESTION_COST * SOFT_SCORE_FACTOR, options, answeredOptionIndex);
    }

    private List<Option> mapToOptions(List<ImpactfulQuestionOption> impactfulQuestionOptions,
                                             AttributeLevelScore attributeLevelScore) {
        return impactfulQuestionOptions.stream().map(e -> {
            double progress = (e.impactfulOptionIndex() - 1) * (1.0/(impactfulQuestionOptions.size() - 1));
            Map<AttributeLevelScore, Double> promisedScores = new HashMap<>();
            promisedScores.put(attributeLevelScore, e.impactfulOptionImpactValue());
            return new Option(e.impactfulOptionId(),
                e.impactfulOptionIndex(),
                promisedScores,
                progress,
                DEFAULT_QUESTION_COST);
        }).toList();
    }

    private record ImpactfulQuestionOption(Long impactfulOptionId,
                                           Integer impactfulOptionIndex,
                                           double impactfulOptionImpactValue) {}
}
