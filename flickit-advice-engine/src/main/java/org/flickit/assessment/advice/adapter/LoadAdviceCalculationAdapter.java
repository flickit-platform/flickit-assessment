package org.flickit.assessment.advice.adapter;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
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

    @Override
    public Plan load(UUID assessmentId, Map<Long, Long> targets) {

        List<AttributeLevelScore> targetList = new ArrayList<>();
        ArrayList<Question> questions = new ArrayList<>();
        for (Map.Entry<Long, Long> target: targets.entrySet()) {
            List<QuestionView> assessedQuestions =
                questionRepository.findAssessedQuestions(assessmentId, target.getKey(), target.getValue());

            Map<Long, List<QuestionView>> collect = assessedQuestions.stream()
                .collect(Collectors.groupingBy(QuestionView::getQuestionId));

            int totalScore = 0;
            for (List<QuestionView> questionViews: collect.values()) {
                Integer i = questionViews.stream().map(QuestionView::getQuestionImpactWeight).findFirst().get();
                totalScore += i;
            }

            List<LevelCompetenceJpaEntity> byAffectedLevelId = levelCompetenceRepository.findByAffectedLevelId(target.getValue());

            for (LevelCompetenceJpaEntity levelCompetenceJpaEntity: byAffectedLevelId) {
                Long id = levelCompetenceJpaEntity.getEffectiveLevel().getId();
                QualityAttributeValueJpaEntity attributeValueEntity =
                    attributeValueRepository.findByAssessmentIdAndAttributeIdAndMaturityLevelId(assessmentId, target.getKey(), id);
                AttributeMaturityScoreJpaEntity attributeMaturityScoreEntity =
                    attributeMaturityScoreRepository.findByAttributeValueIdAndMaturityLevelId(attributeValueEntity.getId(), id).get();

                double currentGain = totalScore * attributeMaturityScoreEntity.getScore();
                int minGain = totalScore * levelCompetenceJpaEntity.getValue();
                targetList.add(new AttributeLevelScore(currentGain, minGain, 0, 0L));
            }

            collect.forEach((questionId, questionViews) -> {
                Integer answerOptionIndex = questionViews.stream()
                    .map(QuestionView::getCurrentOptionIndex)
                    .findFirst()
                    .get();
                List<Option> options = questionViews.stream().map(e -> {
                    int progress = (e.getAnswerOptionIndex() - 1) * ((questionViews.size() - 1) / 100);
                    Option option = new Option(
                        e.getAnswerOptionId(),
                        e.getAnswerOptionIndex(),
                        new HashMap<>(),
                        progress,
                        1);
                    return option;
                }).toList();
                Question question = new Question(questionId, 1, options, answerOptionIndex);
                questions.add(question);
            });

        }
        return new Plan(targetList, questions);
    }
}
