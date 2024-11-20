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
import org.flickit.assessment.data.jpa.kit.question.ImpactfulQuestionView;
import org.flickit.assessment.data.jpa.kit.question.QuestionIdWithAnsweredOptionIndexView;
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
        long kitVersionId = assessmentResult.getKitVersionId();

        for (AttributeLevelTarget attributeLevelTarget : attributeLevelTargets) {
            long attributeId = attributeLevelTarget.getAttributeId();
            long maturityLevelId = attributeLevelTarget.getMaturityLevelId();

            List<LevelCompetenceJpaEntity> levelCompetenceEntities =
                levelCompetenceRepository.findByAffectedLevelIdAndKitVersionId(maturityLevelId, kitVersionId);
            for (LevelCompetenceJpaEntity levelCompetenceEntity : levelCompetenceEntities) {
                Long effectiveLevelId = levelCompetenceEntity.getEffectiveLevelId();
                AttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByAttributeIdAndAssessmentResultId(attributeId, assessmentResult.getId());

                Double gainedScorePercentage = attributeMaturityScoreRepository
                    .findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), effectiveLevelId)
                    .map(AttributeMaturityScoreJpaEntity::getScore)
                    .orElse(DEFAULT_ATTRIBUTE_MATURITY_SCORE);

                var impactfulQuestions = questionRepository.findAdvisableImpactfulQuestions(kitVersionId, attributeId, effectiveLevelId);

                var impactfulQuestionIds = impactfulQuestions.stream()
                    .map(ImpactfulQuestionView::getQuestionId)
                    .collect(toSet());
                var improvableQuestions = questionRepository.findImprovableQuestions(assessmentResult.getId(), kitVersionId, impactfulQuestionIds);
                var improvableImpactfulQuestions = filterImprovableImpactfulQuestions(improvableQuestions, impactfulQuestions);

                Map<Long, Integer> impactfulQuestionIdToQuestionImpact = mapImpactfulQuestionIdToWeight(improvableImpactfulQuestions);
                int totalScore = calculateTotalScore(impactfulQuestionIdToQuestionImpact);
                double gainedScore = totalScore * (gainedScorePercentage/100.0);
                double requiredScore = totalScore * (levelCompetenceEntity.getValue()/100.0);
                AttributeLevelScore attributeLevelScore =
                    new AttributeLevelScore(gainedScore, requiredScore, attributeId, effectiveLevelId);
                attributeLevelScores.add(attributeLevelScore);

                Map<Long, List<ImpactfulQuestionOption>> impactfulQuestionIdToOptions = mapImpactfulQuestionIdToOptions(improvableImpactfulQuestions);
                Map<Long, Integer> impactfulQuestionIdToQuestionAnswer = mapImpactfulQuestionIdToAnswer(improvableQuestions);
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
        var questions = new ArrayList<>(idToQuestions.values());
        questions.sort(getQuestionComparator());
        return new Plan(attributeLevelScores, questions);
    }

    private List<ImpactfulQuestionView> filterImprovableImpactfulQuestions(List<QuestionIdWithAnsweredOptionIndexView> improvableQuestions, List<ImpactfulQuestionView> impactfulQuestions) {
        var improvableQuestionIds = improvableQuestions.stream()
            .map(QuestionIdWithAnsweredOptionIndexView::getQuestionId)
            .collect(toSet());
        return impactfulQuestions.stream()
            .filter(v -> improvableQuestionIds.contains(v.getQuestionId()))
            .toList();
    }

    private Map<Long, Integer> mapImpactfulQuestionIdToWeight(List<ImpactfulQuestionView> impactfulQuestions) {
        Map<Long, Integer> questionIdToWeight = new HashMap<>();
        for (ImpactfulQuestionView question: impactfulQuestions) {
            Long questionId = question.getQuestionId();
            Integer weight = question.getQuestionImpactWeight();
            questionIdToWeight.putIfAbsent(questionId, weight);
        }
        return questionIdToWeight;
    }

    private Map<Long, List<ImpactfulQuestionOption>> mapImpactfulQuestionIdToOptions(List<ImpactfulQuestionView> impactfulQuestions) {
        return impactfulQuestions.stream()
            .collect(groupingBy(ImpactfulQuestionView::getQuestionId,
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

    private Map<Long, Integer> mapImpactfulQuestionIdToAnswer(List<QuestionIdWithAnsweredOptionIndexView> improvableQuestions) {
        return improvableQuestions.stream()
            .filter(v -> v.getAnsweredOptionIndex() != null)
            .collect(toMap(QuestionIdWithAnsweredOptionIndexView::getQuestionId, v -> v.getAnsweredOptionIndex() - 1));
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

    private Comparator<Question> getQuestionComparator() {
        return (q1, q2) -> {
            int compareOptionsSize = Integer.compare(q2.getOptions().size(), q1.getOptions().size());
            if (compareOptionsSize != 0) {
                return compareOptionsSize; // Descending order for options size
            }
            for (int optionIndex = 0; optionIndex < q1.getOptions().size(); optionIndex++) {
                double maxPromisedScore1 = q1.getOptions().get(optionIndex)
                    .getPromisedScores().values().stream()
                    .max(Double::compare).orElse(0.0);

                double maxPromisedScore2 = q2.getOptions().get(optionIndex)
                    .getPromisedScores().values().stream()
                    .max(Double::compare).orElse(0.0);

                int compareMaxScore = Double.compare(maxPromisedScore2, maxPromisedScore1);
                if (compareMaxScore != 0) {
                    return compareMaxScore; // Descending order for max promised score
                }
            }
            return 0;
        };
    }

    private record ImpactfulQuestionOption(Long impactfulOptionId,
                                           Integer impactfulOptionIndex,
                                           double impactfulOptionImpactValue) {}
}
