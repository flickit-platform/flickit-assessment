package org.flickit.assessment.advice.adapter;

import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.Target;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionView;
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

    @Override
    public Plan load(UUID assessmentId, Map<Long, Long> targets) {


        List<Target> targetList = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for (Map.Entry<Long, Long> target: targets.entrySet()) {
            Long maturityLevelId = target.getValue();
            List<LevelCompetenceJpaEntity> byAffectedLevelId = levelCompetenceRepository.findByAffectedLevelId(maturityLevelId);
            for (LevelCompetenceJpaEntity levelCompetenceJpa: byAffectedLevelId) {
                Long targetMaturityLevelId = levelCompetenceJpa.getEffectiveLevel().getId();
                Long attributeId = target.getKey();
                List<QuestionView> assessedQuestions =
                    questionRepository.findAssessedQuestions(assessmentId, attributeId, targetMaturityLevelId);


                Map<Long, List<QuestionView>> collect = assessedQuestions.stream()
                    .collect(Collectors.groupingBy(QuestionView::getQuestionId));

                int totalScore = 0;
                for (List<QuestionView> questionViews: collect.values()) {
                    Integer i = questionViews.stream().map(QuestionView::getQuestionImpactWeight).findFirst().orElse(DEFAULT_QUESTION_IMPACT_WEIGHT);
                    totalScore += i;
                }


                QualityAttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByAssessmentIdAndAttributeIdAndMaturityLevelId(assessmentId, attributeId, targetMaturityLevelId);

                Double score = attributeMaturityScoreRepository
                    .findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), targetMaturityLevelId)
                    .map(AttributeMaturityScoreJpaEntity::getScore).orElse(DEFAULT_ATTRIBUTE_MATURITY_SCORE);

                double currentGain = totalScore * score;
                double minGain = totalScore * (levelCompetenceJpa.getValue()/100.0);
                Target competenceTarget = new Target(currentGain, minGain);
                targetList.add(competenceTarget);

                collect.forEach((questionId, questionViews) -> {
                    Optional<Question> first = questions.stream().filter(e -> e.getId() == questionId).findFirst();
                    if (first.isPresent()) {
                        Question question = first.get();
                        questionViews.forEach(v -> {
                            Option option = question.getOptions().stream()
                                .filter(m -> m.getIndex() == v.getAnswerOptionIndex()).findFirst().get();
                            option.getGains().put(competenceTarget, v.getAnswerOptionImpactValue());
                        });
                    } else {
                        Integer answerOptionIndex = questionViews.stream()
                            .map(QuestionView::getCurrentOptionIndex)
                            .findFirst()
                            .orElse(1);
                        List<Option> options = questionViews.stream().map(e -> {
                            double progress = (e.getAnswerOptionIndex() - 1) * (1.0/(questionViews.size() - 1));
                            Option option = new Option();
                            option.setProgress(progress);
                            option.setQuestionCost(DEFAULT_QUESTION_COST);
                            option.setId(e.getAnswerOptionId());
                            option.setIndex(e.getAnswerOptionIndex());
                            option.setGains(new HashMap<>());
                            option.getGains().put(competenceTarget, e.getAnswerOptionImpactValue());
                            return option;
                        }).toList();
                        Question question = new Question(questionId, DEFAULT_QUESTION_COST, options, answerOptionIndex);
                        questions.add(question);
                    }
                });
            }
        }
        return new Plan(targetList, questions);
    }
}
