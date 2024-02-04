package org.flickit.assessment.advice.adapter.out.calculation;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.ImprovableImpactfulQuestionView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
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
    public Plan loadAdviceCalculationInfo(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        List<AttributeLevelScore> attributeLevelScores = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for (AttributeLevelTarget attributeLevelTarget : attributeLevelTargets) {
            Long attributeId = attributeLevelTarget.attributeId();
            Long maturityLevelId = attributeLevelTarget.maturityLevelId();

            List<LevelCompetenceJpaEntity> levelCompetenceEntities =
                levelCompetenceRepository.findByAffectedLevelId(maturityLevelId);
            for (LevelCompetenceJpaEntity levelCompetenceEntity : levelCompetenceEntities) {
                Long effectiveLevelId = levelCompetenceEntity.getEffectiveLevel().getId();
                var assessmentResultJpaEntity = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(LOAD_ADVICE_CALC_INFO_ASSESSMENT_RESULT_NOT_FOUND));
                QualityAttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByQualityAttributeIdAndAssessmentResult_Id(attributeId, assessmentResultJpaEntity.getId());

                Double gainedScorePercentage = attributeMaturityScoreRepository
                    .findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), effectiveLevelId)
                    .map(AttributeMaturityScoreJpaEntity::getScore)
                    .orElse(DEFAULT_ATTRIBUTE_MATURITY_SCORE);

                List<ImprovableImpactfulQuestionView> impactfulQuestions =
                    questionRepository.findImprovableImpactfulQuestions(assessmentId, attributeId, effectiveLevelId);

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
                    Optional<Question> possibleQuestion = questions.stream()
                        .filter(e -> e.getId() == impactfulQuestionId)
                        .findFirst();
                    if (possibleQuestion.isPresent()) {
                        Question existedQuestion = possibleQuestion.get();
                        addAttrLevelScoreToExistedQuestion(impactfulOptions, existedQuestion, attributeLevelScore);
                    } else {
                        Integer answeredOptionIndex = impactfulQuestionIdToQuestionAnswer.get(impactfulQuestionId);
                        Question question = mapToQuestion(impactfulQuestionId, answeredOptionIndex, impactfulOptions, attributeLevelScore);
                        questions.add(question);
                    }
                });
            }
        }
        return new Plan(attributeLevelScores, questions);
    }

    private static Map<Long, Integer> mapImpactfulQuestionIdToWeight(List<ImprovableImpactfulQuestionView> impactfulQuestions) {
        Map<Long, List<ImprovableImpactfulQuestionView>> questionInfoGroupedById = impactfulQuestions.stream()
            .collect(Collectors.groupingBy(ImprovableImpactfulQuestionView::getImpactfulQuestionId));
        Map<Long, Integer> questionIdToWeight = new HashMap<>();
        questionInfoGroupedById.forEach((questionId, questionInfo) -> {
            Integer questionImpactWeight = questionInfo.get(0).getImpactfulQuestionImpactWeight();
            questionIdToWeight.put(questionId, questionImpactWeight);
        });
        return questionIdToWeight;
    }

    private static Map<Long, List<ImpactfulQuestionOption>> mapImpactfulQuestionIdToOptions(List<ImprovableImpactfulQuestionView> impactfulQuestions) {
        return impactfulQuestions.stream()
            .collect(Collectors.groupingBy(ImprovableImpactfulQuestionView::getImpactfulQuestionId,
                Collectors.mapping(
                    impactfulQuestion -> new ImpactfulQuestionOption(
                        impactfulQuestion.getImpactfulOptionId(),
                        impactfulQuestion.getImpactfulOptionIndex(),
                        impactfulQuestion.getImpactfulOptionImpactValue()
                    ),
                    Collectors.toList()
                )));
    }

    private static Map<Long, Integer> mapImpactfulQuestionIdToAnswer(List<ImprovableImpactfulQuestionView> impactfulQuestions) {
        Map<Long, List<ImprovableImpactfulQuestionView>> questionInfoGroupedById = impactfulQuestions.stream()
            .collect(Collectors.groupingBy(ImprovableImpactfulQuestionView::getImpactfulQuestionId));
        Map<Long, Integer> questionIdToQuestionAnswer = new HashMap<>();
        questionInfoGroupedById.forEach((questionId, questionInfo) -> {
            Integer answeredOptionIndex = questionInfo.get(0).getImpactfulAnsweredOptionIndex();
            if (answeredOptionIndex != null) {
                answeredOptionIndex -= 1;
            }
            questionIdToQuestionAnswer.put(questionId, answeredOptionIndex);
        });
        return questionIdToQuestionAnswer;
    }

    private static int calculateTotalScore(Map<Long, Integer> questionIdToQuestionImpact) {
        return questionIdToQuestionImpact.values()
            .stream()
            .reduce(0, Integer::sum);
    }

    private static void addAttrLevelScoreToExistedQuestion(List<ImpactfulQuestionOption> options,
                                                           Question question,
                                                           AttributeLevelScore attributeLevelScore) {
        options.forEach(v -> {
            Option option = question.getOptions().stream()
                .filter(m -> m.getIndex() == v.impactfulOptionIndex)
                .findFirst()
                .get();
            option.getPromisedScores().put(attributeLevelScore, v.impactfulOptionImpactValue);
        });
    }

    private static Question mapToQuestion(Long effectiveQuestionId,
                                          Integer answeredOptionIndex,
                                          List<ImpactfulQuestionOption> impactfulQuestionOptions,
                                          AttributeLevelScore attributeLevelScore) {
        List<Option> options = mapToOptions(impactfulQuestionOptions, attributeLevelScore);
        return new Question(effectiveQuestionId, DEFAULT_QUESTION_COST, options, answeredOptionIndex);
    }

    private static List<Option> mapToOptions(List<ImpactfulQuestionOption> impactfulQuestionOptions,
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
