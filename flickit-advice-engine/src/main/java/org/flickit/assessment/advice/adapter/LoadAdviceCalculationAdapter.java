package org.flickit.assessment.advice.adapter;

import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.EffectiveQuestionOnAdviceView;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadAdviceCalculationAdapter implements LoadAdviceCalculationInfoPort {

    private final QuestionJpaRepository questionRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;
    private final QualityAttributeValueJpaRepository attributeValueRepository;

    private static final int DEFAULT_QUESTION_IMPACT_WEIGHT = 1;
    private static final double DEFAULT_ATTRIBUTE_MATURITY_SCORE = 0.0;
    private static final int DEFAULT_QUESTION_COST = 1;
    private static final int DEFAULT_ANSWER_OPTION_INDEX = 1;

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

                List<EffectiveQuestionOnAdviceView> effectiveQuestions =
                    questionRepository.findEffectiveQuestionsOnAdvice(assessmentId, attributeId, effectiveLevelId);

                Map<Long, List<EffectiveQuestionOnAdviceView>> questionInfoGroupedById = effectiveQuestions.stream()
                    .collect(Collectors.groupingBy(EffectiveQuestionOnAdviceView::getQuestionId));

                QualityAttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByAssessmentIdAndAttributeIdAndMaturityLevelId(assessmentId ,
                        attributeId,
                        effectiveLevelId);

                Double gainedScorePercentage = attributeMaturityScoreRepository
                    .findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), effectiveLevelId)
                    .map(AttributeMaturityScoreJpaEntity::getScore)
                    .orElse(DEFAULT_ATTRIBUTE_MATURITY_SCORE);

                int totalScore = calculateTotalScore(questionInfoGroupedById);
                double gainedScore = totalScore * (gainedScorePercentage/100.0);
                double requiredScore = totalScore * (levelCompetenceEntity.getValue()/100.0);
                AttributeLevelScore attributeLevelScore =
                    new AttributeLevelScore(gainedScore, requiredScore, attributeId, effectiveLevelId);
                attributeLevelScores.add(attributeLevelScore);

                questionInfoGroupedById.forEach((questionId, questionViews) -> {
                    Optional<Question> possibleQuestion = questions.stream()
                        .filter(e -> e.getId() == questionId)
                        .findFirst();
                    if (possibleQuestion.isPresent()) {
                        Question question = possibleQuestion.get();
                        addAttrLevelScoreToExistedQuestion(questionViews, question, attributeLevelScore);
                    } else {
                        Question question = mapToQuestion(questionId, questionViews, attributeLevelScore);
                        questions.add(question);
                    }
                });
            }
        }
        return new Plan(attributeLevelScores, questions);
    }

    private static int calculateTotalScore(Map<Long, List<EffectiveQuestionOnAdviceView>> questionInfoGroupedById) {
        int totalScore = 0;
        Collection<List<EffectiveQuestionOnAdviceView>> questionsWithRepetitiveImpact = questionInfoGroupedById.values();
        for (List<EffectiveQuestionOnAdviceView> questionWithRepetitiveImpact: questionsWithRepetitiveImpact) {
            Integer questionImpactWeight = questionWithRepetitiveImpact.stream()
                .map(EffectiveQuestionOnAdviceView::getQuestionImpactWeight)
                .findFirst()
                .orElse(DEFAULT_QUESTION_IMPACT_WEIGHT);
            totalScore += questionImpactWeight;
        }
        return totalScore;
    }

    private static void addAttrLevelScoreToExistedQuestion(List<EffectiveQuestionOnAdviceView> effectiveQuestionOnAdviceViews,
                                                           Question question,
                                                           AttributeLevelScore attributeLevelScore) {
        effectiveQuestionOnAdviceViews.forEach(v -> {
            Option option = question.getOptions().stream()
                .filter(m -> m.getIndex() == v.getAnswerOptionIndex())
                .findFirst()
                .get();
            option.getPromisedScores().put(attributeLevelScore, v.getAnswerOptionImpactValue());
        });
    }

    @NotNull
    private static Question mapToQuestion(Long questionId,
                                          List<EffectiveQuestionOnAdviceView> effectiveQuestionOnAdviceViews,
                                          AttributeLevelScore attributeLevelScore) {
        Integer answerOptionIndex = effectiveQuestionOnAdviceViews.stream()
            .map(EffectiveQuestionOnAdviceView::getCurrentOptionIndex)
            .findFirst()
            .orElse(DEFAULT_ANSWER_OPTION_INDEX);
        List<Option> options = mapToOptions(effectiveQuestionOnAdviceViews, attributeLevelScore);
        return new Question(questionId, DEFAULT_QUESTION_COST, options, answerOptionIndex);
    }

    @NotNull
    private static List<Option> mapToOptions(List<EffectiveQuestionOnAdviceView> effectiveQuestionOnAdviceViews,
                                             AttributeLevelScore attributeLevelScore) {
        return effectiveQuestionOnAdviceViews.stream().map(e -> {
            double progress = (e.getAnswerOptionIndex() - 1) * (1.0/(effectiveQuestionOnAdviceViews.size() - 1));
            Map<AttributeLevelScore, Double> promisedScores = new HashMap<>();
            promisedScores.put(attributeLevelScore, e.getAnswerOptionImpactValue());
            return new Option(e.getAnswerOptionId(),
                e.getAnswerOptionIndex(),
                promisedScores,
                progress,
                DEFAULT_QUESTION_COST);
        }).toList();
    }
}
